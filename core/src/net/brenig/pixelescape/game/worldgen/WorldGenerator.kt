package net.brenig.pixelescape.game.worldgen

import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.worldgen.predefined.IScoreWorldFeature
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator
import net.brenig.pixelescape.game.worldgen.special.ISpecialWorldGenerator
import net.brenig.pixelescape.game.worldgen.terrain.*
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.lib.error
import java.util.*

/**
 * Generates terrain and obstacles
 */
class WorldGenerator(private val world: World, private val gameMode: GameMode) {

    private val terrainGenerators = WeightedList<ITerrainGenerator>()

    private val specialGenerators = ArrayList<ISpecialWorldGenerator>()
    private val specialPredefinedGenerators = ArrayList<IScoreWorldFeature>()

    ////////////////////////////////////////////////
    // Worldgen effects (--> eg. items/Gamemodes) //
    ////////////////////////////////////////////////
    var obstacleSizeModifier = 1f

    /**
     * registers a new TerrainGenerator
     *
     * @param generator The Generator to register
     */
    fun registerTerrainGenerator(generator: ITerrainGenerator) {
        if (generator.weight <= 0) return
        terrainGenerators.add(generator.weight, generator)
    }

    /**
     * generates the World
     *
     * @param blockToGenerate  amount of blocks that should get generated
     * @param generationPasses amount of tries to generate the world
     * @param random           world random-generator
     */
    fun generateWorld(blockToGenerate: Int, generationPasses: Int, random: Random) {
        var blockToGenerate = blockToGenerate
        var generationPasses = generationPasses
        if (blockToGenerate > 0) {
            //Init available terrain gen list
            val gens = terrainGenerators.createCopy()

            while (generationPasses > 0 && blockToGenerate > 0) {
                //get last terrain
                var lastGeneratedTerrainPair = world.terrain.newest
                if (lastGeneratedTerrainPair == null) {
                    lastGeneratedTerrainPair = TerrainPair(Reference.STARTING_TERRAIN_HEIGHT, Reference.STARTING_TERRAIN_HEIGHT)
                }

                val oldTerrainBufferIndexStart = world.terrainBufferWorldIndex
                val gen = gens.getRandomValueWithFilter(random, WorldGenFilter(lastGeneratedTerrainPair, blockToGenerate)) ?: //no applicable generators left
                        break
                val generated = gen.generate(world, lastGeneratedTerrainPair, blockToGenerate, world.terrainBufferWorldIndex, random)
                blockToGenerate -= generated
                generationPasses--
                if (blockToGenerate < 0) {
                    error("Invalid World Gen!! Generator ignoring MAX! Generator: " + gen)
                }
                if (oldTerrainBufferIndexStart + generated != world.terrainBufferWorldIndex) {
                    error("Invalid World Gen!! Generator return value invalid! Generator: " + gen + "; generated: " + generated + "; lastGen: " + oldTerrainBufferIndexStart + "; currentGen: " + world.terrainBufferWorldIndex)
                }
            }
        }
        for (gen in specialGenerators) {
            gen.generate(this, world, random, gameMode)
        }
    }

    fun addSpecialGenerator(gen: ISpecialWorldGenerator) {
        specialGenerators.add(gen)
    }

    fun reset() {
        for (generator in specialGenerators) {
            generator.reset(world)
        }
        for (gen in specialPredefinedGenerators) {
            gen.reset(world)
        }
    }


    /**
     * registers the default World-generators
     */
    fun registerDefaultWorldGenerators() {
        registerDefaultTerrainGenerators()
        registerDefaultBarricadeGenerator()
    }

    /**
     * registers the default terrain generators with default weights
     */
    fun registerDefaultTerrainGenerators() {
        registerTerrainGenerator(RandomTerrainGenerator(9))
        registerTerrainGenerator(FlatCorridor(3))
        registerTerrainGenerator(TerrainOpening(1))
        registerTerrainGenerator(TerrainClosing(4))
        registerTerrainGenerator(DiagonalCorridor(7))
    }


    /**
     * registers the default Barricade generator
     */
    fun registerDefaultBarricadeGenerator() {
        addSpecialGenerator(BarricadeGenerator((Reference.TARGET_RESOLUTION_X * 0.85f).toInt()))
    }

    private inner class WorldGenFilter constructor(private val old: TerrainPair, private val blockToGenerate: Int) : WeightedList.Filter<ITerrainGenerator> {

        override fun isValid(value: ITerrainGenerator): Boolean {
            val genLength = value.getMinGenerationLength(old)
            return !(genLength <= 0 || genLength > blockToGenerate)
        }
    }
}

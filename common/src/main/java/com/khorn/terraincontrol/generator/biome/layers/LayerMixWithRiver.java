package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public class LayerMixWithRiver extends Layer
{
    public LayerMixWithRiver(long paramLong, Layer paramGenLayer, Layer riverLayer, WorldSettings configs, LocalWorld world)
    {
        super(paramLong);
        this.child = paramGenLayer;
        this.configs = configs;
        this.riverLayer = riverLayer;
        this.riverBiomes = new int[world.getMaxBiomesCount()];

        for (int id = 0; id < this.riverBiomes.length; id++)
        {
            LocalBiome biome = configs.biomes[id];

            if (biome == null || biome.getBiomeConfig().riverBiome.isEmpty())
                this.riverBiomes[id] = -1;
            else
                this.riverBiomes[id] = world.getBiomeByName(biome.getBiomeConfig().riverBiome).getIds().getGenerationId();

        }
    }

    private WorldSettings configs;
    private int[] riverBiomes;
    private Layer riverLayer;

    @Override
    public void SetWorldSeed(long seed)
    {
        super.SetWorldSeed(seed);
        riverLayer.SetWorldSeed(seed + 31337);
    }

    @Override
    public int[] GetBiomes(ArraysCache arraysCache, int x, int z, int x_size, int z_size)
    {
        switch (arraysCache.outputType)
        {
            case FULL:
                return this.GetFull(arraysCache, x, z, x_size, z_size);
            case WITHOUT_RIVERS:
                return this.GetWithoutRivers(arraysCache, x, z, x_size, z_size);
            case ONLY_RIVERS:
                return this.GetOnlyRivers(arraysCache, x, z, x_size, z_size);
            default:
                throw new UnsupportedOperationException("Unknown/invalid output type: " + arraysCache.outputType);
        }
    }

    private int[] GetFull(ArraysCache arraysCache, int x, int z, int x_size, int z_size)
    {
        int[] arrayOfInt1 = this.child.GetBiomes(arraysCache, x, z, x_size, z_size);
        int[] arrayOfInt2 = this.riverLayer.GetBiomes(arraysCache, x, z, x_size, z_size);
        int[] arrayOfInt3 = arraysCache.GetArray(x_size * z_size);
        WorldConfig worldConfig = this.configs.worldConfig;

        int currentPiece;
        int currentRiver;
        int cachedId;
        for (int i = 0; i < z_size; i++)
        {
            for (int j = 0; j < x_size; j++)
            {
                currentPiece = arrayOfInt1[(j + i * x_size)];
                currentRiver = arrayOfInt2[(j + i * x_size)];

                if ((currentPiece & LandBit) != 0)
                    cachedId = currentPiece & BiomeBits;
                else if (worldConfig.FrozenOcean && (currentPiece & IceBit) != 0)
                    cachedId = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    cachedId = DefaultBiome.OCEAN.Id;

                if (worldConfig.riversEnabled && (currentRiver & RiverBits) != 0 && !this.configs.biomes[cachedId].getBiomeConfig().riverBiome.isEmpty())
                    currentPiece = this.riverBiomes[cachedId];
                else
                    currentPiece = cachedId;

                arrayOfInt3[(j + i * x_size)] = currentPiece;
            }
        }

        return arrayOfInt3;

    }

    private int[] GetWithoutRivers(ArraysCache arraysCache, int x, int z, int x_size, int z_size)
    {
        int[] arrayOfInt1 = this.child.GetBiomes(arraysCache, x, z, x_size, z_size);
        //int[] arrayOfInt2 = this.riverLayer.GetBiomes(arraysCache, x, z, x_size, z_size);
        int[] arrayOfInt3 = arraysCache.GetArray(x_size * z_size);
        WorldConfig worldConfig = this.configs.worldConfig;

        int currentPiece;
        // int currentRiver;
        int cachedId;
        for (int i = 0; i < z_size; i++)
        {
            for (int j = 0; j < x_size; j++)
            {
                currentPiece = arrayOfInt1[(j + i * x_size)];
                // currentRiver = arrayOfInt2[(j + i * x_size)];

                if ((currentPiece & LandBit) != 0)
                    cachedId = currentPiece & BiomeBits;
                else if (worldConfig.FrozenOcean && (currentPiece & IceBit) != 0)
                    cachedId = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    cachedId = DefaultBiome.OCEAN.Id;

                /*if (this.worldConfig.riversEnabled && (currentRiver & RiverBits) != 0 && !this.worldConfig.biomeConfigs[cachedId].riverBiome.isEmpty())
                    currentPiece = this.riverBiomes[cachedId];
                else*/
                currentPiece = cachedId;

                arrayOfInt3[(j + i * x_size)] = currentPiece;
            }
        }

        return arrayOfInt3;
    }

    private int[] GetOnlyRivers(ArraysCache arraysCache, int x, int z, int x_size, int z_size)
    {
        int[] arrayOfInt1 = this.child.GetBiomes(arraysCache, x, z, x_size, z_size);
        int[] arrayOfInt2 = this.riverLayer.GetBiomes(arraysCache, x, z, x_size, z_size);
        int[] arrayOfInt3 = arraysCache.GetArray(x_size * z_size);
        WorldConfig worldConfig = this.configs.worldConfig;

        int currentPiece;
        int currentRiver;
        int cachedId;
        for (int i = 0; i < z_size; i++)
        {
            for (int j = 0; j < x_size; j++)
            {
                currentPiece = arrayOfInt1[(j + i * x_size)];
                currentRiver = arrayOfInt2[(j + i * x_size)];

                if ((currentPiece & LandBit) != 0)
                    cachedId = currentPiece & BiomeBits;
                else if (worldConfig.FrozenOcean && (currentPiece & IceBit) != 0)
                    cachedId = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    cachedId = DefaultBiome.OCEAN.Id;

                if (worldConfig.riversEnabled && (currentRiver & RiverBits) != 0 && !this.configs.biomes[cachedId].getBiomeConfig().riverBiome.isEmpty())
                    currentPiece = 1;
                else
                    currentPiece = 0;

                arrayOfInt3[(j + i * x_size)] = currentPiece;
            }
        }

        return arrayOfInt3;
    }
}

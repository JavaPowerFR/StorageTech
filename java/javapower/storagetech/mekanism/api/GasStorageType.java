package javapower.storagetech.mekanism.api;

public enum GasStorageType
{
    SIXTY_FOUR_K("64k", 64_000),
    TWO_HUNDRED_FIFTY_SIX_K("256k", 256_000),
    THOUSAND_TWENTY_FOUR_K("1024k", 1024_000),
    FOUR_THOUSAND_NINETY_SIX_K("4096k", 4096_000),
    CREATIVE("creative", Long.MAX_VALUE-1);

    private final String name;
    private final long capacity;

    GasStorageType(String name, long capacity)
    {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName()
    {
        return name;
    }

    public long getCapacity()
    {
        return capacity;
    }
}
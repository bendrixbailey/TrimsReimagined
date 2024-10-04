package com.trimsreimagined.utils;

public interface PlayerMixinMethodAccess {
    Integer trimsreimagined$getTrimCountForTrimType(String type);

    void trimsreimagined$toggleTrimEffects();
    boolean trimsreimagined$getTrimEffectStatus();
}

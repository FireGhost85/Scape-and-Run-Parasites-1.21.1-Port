package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;

/** A parasite with extra hit boxes (1.12 IEntityMultiPart); the parts are exposed through {@code getParts()}. */
public interface IHitboxedEntity {
    public EntityParasiteBase getParent();
}

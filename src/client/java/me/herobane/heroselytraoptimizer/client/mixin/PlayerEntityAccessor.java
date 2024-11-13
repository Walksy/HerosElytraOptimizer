package me.herobane.heroselytraoptimizer.client.mixin;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor {
    @Invoker("canChangeIntoPose")
    boolean invokeCanChangeIntoPose(EntityPose pose);
}

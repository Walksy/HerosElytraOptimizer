package me.herobane.heroselytraoptimizer.client.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Unique
    private boolean isWearingElytra() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    protected void updatePose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        EntityPose entityPose;
        if (player.isFallFlying() && isWearingElytra()) {
            entityPose = EntityPose.FALL_FLYING;
        } else if (player.isFallFlying() && !isWearingElytra()) {
            entityPose = EntityPose.STANDING;
        } else if (player.isSleeping()) {
            entityPose = EntityPose.SLEEPING;
        } else if (player.isSwimming()) {
            entityPose = EntityPose.SWIMMING;
        } else if (player.isUsingRiptide()) {
            entityPose = EntityPose.SPIN_ATTACK;
        } else if (player.isSneaking() && !player.getAbilities().flying) {
            entityPose = EntityPose.CROUCHING;
        } else {
            entityPose = EntityPose.STANDING;
        }

        EntityPose entityPose2;
        PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
        if (!player.isSpectator() && !player.hasVehicle() && !accessor.invokeCanChangeIntoPose(entityPose)) {
            if (accessor.invokeCanChangeIntoPose(EntityPose.CROUCHING)) {
                entityPose2 = EntityPose.CROUCHING;
            } else {
                entityPose2 = EntityPose.SWIMMING;
            }
        } else {
            entityPose2 = entityPose;
        }

        player.setPose(entityPose2);
        ci.cancel(); // Cancels the original updatePose to prevent overwriting
    }
}

package me.herobane.heroselytraoptimizer.client.mixin;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow protected abstract boolean canChangeIntoPose(EntityPose pose);

    @Shadow @Final private PlayerAbilities abilities;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    public void updatePos(CallbackInfo ci)
    {
        if (this.canChangeIntoPose(EntityPose.SWIMMING)) {
            EntityPose entityPose;
            PlayerEntity p = PlayerEntity.class.cast(this);
            if (this.isFallFlying() && this.isWearingElytra(p)) {
                entityPose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entityPose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                entityPose = EntityPose.SWIMMING;
            } else if (this.isUsingRiptide()) {
                entityPose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !this.abilities.flying) {
                entityPose = EntityPose.CROUCHING;
            } else {
                entityPose = EntityPose.STANDING;
            }

            EntityPose entityPose2;
            if (!this.isSpectator() && !this.hasVehicle() && !this.canChangeIntoPose(entityPose)) {
                if (this.canChangeIntoPose(EntityPose.CROUCHING)) {
                    entityPose2 = EntityPose.CROUCHING;
                } else {
                    entityPose2 = EntityPose.SWIMMING;
                }
            } else {
                entityPose2 = entityPose;
            }

            this.setPose(entityPose2);
        }
        ci.cancel();
    }

    @Unique
    private boolean isWearingElytra(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }
}

package com.benbenlaw.sniffysniffers.entity;

import com.benbenlaw.sniffysniffers.block.SSBlocks;
import com.benbenlaw.sniffysniffers.core.ChanceResult;
import com.benbenlaw.sniffysniffers.datamaps.SSDataMaps;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SniffySnifferEntity extends Animal {
    private static final Brain.Provider<SniffySnifferEntity> BRAIN_PROVIDER;
    private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
    private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
    private static final int SNIFFER_BABY_START_AGE = -48000;
    private static final float DIGGING_BB_HEIGHT_OFFSET = 0.4F;
    private static final EntityDimensions DIGGING_DIMENSIONS;
    private static final EntityDataAccessor<Sniffer.State> DATA_STATE;
    private static final EntityDataAccessor<Integer> DATA_DROP_SEED_AT_TICK;
    public final AnimationState feelingHappyAnimationState = new AnimationState();
    public final AnimationState scentingAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState risingAnimationState = new AnimationState();

    public final TagKey<Block> targetBlock = Tags.Blocks.STORAGE_BLOCKS_DIAMOND;

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.1F).add(Attributes.MAX_HEALTH, (double)14.0F);
    }

    public SniffySnifferEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.ON_TOP_OF_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_CAUTIOUS, -1.0F);
    }

    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_STATE, Sniffer.State.IDLING);
        entityData.define(DATA_DROP_SEED_AT_TICK, 0);
    }

    public void onPathfindingStart() {
        super.onPathfindingStart();
        if (this.isOnFire() || this.isInWater()) {
            this.setPathfindingMalus(PathType.WATER, 0.0F);
        }

    }

    public void onPathfindingDone() {
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    public int getBabyStartAge() {
        return SNIFFER_BABY_START_AGE;
    }

    public EntityDimensions getDefaultDimensions(Pose pose) {
        return this.getState() == Sniffer.State.DIGGING ? DIGGING_DIMENSIONS.scale(this.getAgeScale()) : super.getDefaultDimensions(pose);
    }

    public boolean isSearching() {
        return this.getState() == Sniffer.State.SEARCHING;
    }

    public boolean isTempted() {
        return (Boolean)this.brain.getMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
    }

    public boolean canSniff() {
        return !this.isTempted() && !this.isPanicking() && !this.isInWater() && !this.isInLove() && this.onGround() && !this.isPassenger() && !this.isLeashed();
    }

    public boolean canPlayDiggingSound() {
        return this.getState() == Sniffer.State.DIGGING || this.getState() == Sniffer.State.SEARCHING;
    }

    private BlockPos getHeadBlock() {
        Vec3 position = this.getHeadPosition();
        return BlockPos.containing(position.x(), this.getY() + (double)0.2F, position.z());
    }

    private Vec3 getHeadPosition() {
        return this.position().add(this.getForward().scale((double)2.25F));
    }

    public boolean supportQuadLeash() {
        return true;
    }

    public Vec3[] getQuadLeashOffsets() {
        return Leashable.createQuadLeashOffsets(this, -0.01, 0.63, 0.38, 1.15);
    }

    private Sniffer.State getState() {
        return (Sniffer.State)this.entityData.get(DATA_STATE);
    }

    private SniffySnifferEntity setState(Sniffer.State state) {
        this.entityData.set(DATA_STATE, state);
        return this;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_STATE.equals(accessor)) {
            Sniffer.State state = this.getState();
            this.resetAnimations();
            switch (state.ordinal()) {
                case 1:
                    this.feelingHappyAnimationState.startIfStopped(this.tickCount);
                    break;
                case 2:
                    this.scentingAnimationState.startIfStopped(this.tickCount);
                    break;
                case 3:
                    this.sniffingAnimationState.startIfStopped(this.tickCount);
                case 4:
                default:
                    break;
                case 5:
                    this.diggingAnimationState.startIfStopped(this.tickCount);
                    break;
                case 6:
                    this.risingAnimationState.startIfStopped(this.tickCount);
            }

            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(accessor);
    }

    private void resetAnimations() {
        this.diggingAnimationState.stop();
        this.sniffingAnimationState.stop();
        this.risingAnimationState.stop();
        this.feelingHappyAnimationState.stop();
        this.scentingAnimationState.stop();
    }

    public SniffySnifferEntity transitionTo(Sniffer.State state) {
        switch (state.ordinal()) {
            case 0:
                this.setState(Sniffer.State.IDLING);
                break;
            case 1:
                this.playSound(SoundEvents.SNIFFER_HAPPY, 1.0F, 1.0F);
                this.setState(Sniffer.State.FEELING_HAPPY);
                break;
            case 2:
                this.setState(Sniffer.State.SCENTING).onScentingStart();
                break;
            case 3:
                this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0F, 1.0F);
                this.setState(Sniffer.State.SNIFFING);
                break;
            case 4:
                this.setState(Sniffer.State.SEARCHING);
                break;
            case 5:
                this.setState(Sniffer.State.DIGGING).onDiggingStart();
                break;
            case 6:
                this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
                this.setState(Sniffer.State.RISING);
        }

        return this;
    }

    private SniffySnifferEntity onScentingStart() {
        this.playSound(SoundEvents.SNIFFER_SCENTING, 1.0F, this.isBaby() ? 1.3F : 1.0F);
        return this;
    }

    private SniffySnifferEntity onDiggingStart() {
        this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + DIGGING_DROP_SEED_OFFSET_TICKS);
        //this.level().broadcastEntityEvent(this, (byte)63);
        return this;
    }

    public SniffySnifferEntity onDiggingComplete(boolean success) {
        if (success) {
            this.storeExploredPosition(this.getOnPos());
        }

        return this;
    }

    Optional<BlockPos> calculateDigPosition() {
        BlockPos center = this.getOnPos();
        List<BlockPos> potentialBlocks = new ArrayList<>();

        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos target = center.offset(x, y, z);
                    BlockState state = this.level().getBlockState(target);

                    if (state.typeHolder().getData(SSDataMaps.SNIFFER_LOOT) != null) {
                        if (this.getExploredPositions().noneMatch(p -> p.pos().equals(target))) {
                            potentialBlocks.add(target);
                        }
                    }
                }
            }
        }

        if (potentialBlocks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(potentialBlocks.get(this.random.nextInt(potentialBlocks.size())));
    }

    private boolean canDig(BlockPos position) {
        BlockState state = this.level().getBlockState(position);

        boolean hasLootData = state.typeHolder().getData(SSDataMaps.SNIFFER_LOOT) != null;

        boolean notExplored = this.getExploredPositions().noneMatch((explored) ->
                GlobalPos.of(this.level().dimension(), position).equals(explored));

        return hasLootData && notExplored && this.onGround();
    }

    boolean canDig() {
        return !this.isPanicking() && !this.isTempted() && !this.isBaby() && !this.isInWater() && this.onGround() && !this.isPassenger() && this.canDig(this.getHeadBlock().below());
    }

    private void dropSeed() {
        if (this.level() instanceof ServerLevel level) {
            if (this.entityData.get(DATA_DROP_SEED_AT_TICK) == this.tickCount) {
                BlockPos head = this.getHeadBlock();
                BlockPos dugPos = head.below();
                BlockState dugState = level.getBlockState(dugPos);

                List<ChanceResult> chanceResults = dugState.typeHolder().getData(SSDataMaps.SNIFFER_LOOT);

                if (chanceResults == null || chanceResults.isEmpty()) {
                    return;
                }

                for (ChanceResult result : chanceResults) {
                    ItemStack itemStack = result.rollOutput(this.random);
                    ItemEntity entity = new ItemEntity(level, head.getX(), head.getY(), head.getZ(), itemStack);
                    entity.setDefaultPickUpDelay();
                    level.addFreshEntity(entity);
                }

                this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
            }
        }
    }

    private SniffySnifferEntity emitDiggingParticles(AnimationState state) {
        boolean emit = state.getTimeInMillis((float)this.tickCount) > DIGGING_PARTICLES_DELAY_TICKS && state.getTimeInMillis((float)this.tickCount) < DIGGING_PARTICLES_DURATION_TICKS;
        if (emit) {
            BlockPos head = this.getHeadBlock();
            BlockState stateBelow = this.level().getBlockState(head.below());
            if (stateBelow.getRenderShape() != RenderShape.INVISIBLE) {
                for(int i = 0; i < DIGGING_PARTICLES_AMOUNT; ++i) {
                    Vec3 centered = Vec3.atCenterOf(head).add(0.0F, -0.65F, 0.0F);
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, stateBelow), centered.x, centered.y, centered.z, 0.0F, 0.0F, 0.0F);
                }

                if (this.tickCount % 10 == 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), stateBelow.getSoundType(this.level(), head.below(), this).getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
                }
            }
        }

        if (this.tickCount % 10 == 0) {
            this.level().gameEvent(GameEvent.ENTITY_ACTION, this.getHeadBlock(), GameEvent.Context.of(this));
        }

        return this;
    }

    private SniffySnifferEntity storeExploredPosition(BlockPos position) {
        List<GlobalPos> updated = (List)this.getExploredPositions().limit(20L).collect(Collectors.toList());
        updated.add(0, GlobalPos.of(this.level().dimension(), position));
        this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, updated);
        return this;
    }

    private Stream<GlobalPos> getExploredPositions() {
        return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
    }

    public void jumpFromGround() {
        super.jumpFromGround();
        double speedModifier = this.moveControl.getSpeedModifier();
        if (speedModifier > (double)0.0F) {
            double current = this.getDeltaMovement().horizontalDistanceSqr();
            if (current < 0.01) {
                this.moveRelative(0.1F, new Vec3((double)0.0F, (double)0.0F, (double)1.0F));
            }
        }

    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        ItemStack itemStack = new ItemStack(SSBlocks.SNIFFY_SNIFFER_EGG.get());
        ItemEntity eggEntity = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), itemStack);
        eggEntity.setDefaultPickUpDelay();
        this.finalizeSpawnChildFromBreeding(level, partner, null);
        this.playSound(SoundEvents.SNIFFER_EGG_PLOP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
        level.addFreshEntity(eggEntity);
    }
    public void die(DamageSource source) {
        this.transitionTo(Sniffer.State.IDLING);
        super.die(source);
    }

    @Override
    public void tick() {
        switch (this.getState().ordinal()) {
            case 4 -> this.playSearchingSound();
            case 5 -> this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
        }

        super.tick();
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean isFood = this.isFood(heldItem);
        InteractionResult interactionResult = super.mobInteract(player, hand);
        if (interactionResult.consumesAction() && isFood) {
            this.playEatingSound();
        }

        return interactionResult;
    }

    protected void playEatingSound() {
        this.level().playSound((Entity)null, this, SoundEvents.SNIFFER_EAT, SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level().getRandom(), 0.8F, 1.2F));
    }

    private void playSearchingSound() {
        if (this.level().isClientSide() && this.tickCount % 20 == 0) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_SEARCHING, this.getSoundSource(), 1.0F, 1.0F, false);
        }

    }

    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return Set.of(Sniffer.State.DIGGING, Sniffer.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.SNIFFER_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SNIFFER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SNIFFER_DEATH;
    }

    public int getMaxHeadYRot() {
        return 50;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        SniffySnifferEntity baby = SSEntities.SNIFFY_SNIFFER.get().create(level, EntitySpawnReason.BREEDING);

        if (baby != null) {
            baby.setAge(-24000);
        }

        return baby;
    }

    @Override
    public void setBaby(boolean baby) {
        this.setAge(baby ? -24000 : 0);
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public boolean canMate(Animal partner) {
        if (!(partner instanceof SniffySnifferEntity snifferPartner)) {
            return false;
        } else {
            Set<Sniffer.State> states = Set.of(Sniffer.State.IDLING, Sniffer.State.SCENTING, Sniffer.State.FEELING_HAPPY);
            return states.contains(this.getState()) && states.contains(snifferPartner.getState()) && super.canMate(partner);
        }
    }

    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.SNIFFER_FOOD);
    }

    protected Brain<SniffySnifferEntity> makeBrain(Brain.Packed packedBrain) {
        return BRAIN_PROVIDER.makeBrain(this, packedBrain);
    }

    public Brain<SniffySnifferEntity> getBrain() {
        return (Brain<SniffySnifferEntity>) super.getBrain();
    }

    protected void customServerAiStep(ServerLevel level) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("snifferBrain");
        this.getBrain().tick(level, this);
        profiler.popPush("snifferActivityUpdate");
        SniffySnifferAi.updateActivity(this);
        profiler.pop();
        super.customServerAiStep(level);
    }

    static {
        BRAIN_PROVIDER = Brain.provider(List.of(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS), List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_PLAYERS, SensorType.FOOD_TEMPTATIONS), (var0) -> SniffySnifferAi.getActivities());
        DIGGING_DIMENSIONS = EntityDimensions.scalable(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - DIGGING_BB_HEIGHT_OFFSET).withEyeHeight(0.81F);
        DATA_STATE = SynchedEntityData.defineId(SniffySnifferEntity.class, EntityDataSerializers.SNIFFER_STATE);
        DATA_DROP_SEED_AT_TICK = SynchedEntityData.defineId(SniffySnifferEntity.class, EntityDataSerializers.INT);
    }

    public static enum State {
        IDLING(0),
        FEELING_HAPPY(1),
        SCENTING(2),
        SNIFFING(3),
        SEARCHING(4),
        DIGGING(5),
        RISING(6);

        public static final IntFunction<SniffySnifferEntity.State> BY_ID = ByIdMap.continuous(SniffySnifferEntity.State::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, SniffySnifferEntity.State> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SniffySnifferEntity.State::id);
        private final int id;

        private State(int id) {
            this.id = id;
        }

        public int id() {
            return this.id;
        }
    }
}

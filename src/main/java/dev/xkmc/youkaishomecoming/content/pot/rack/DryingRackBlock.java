package dev.xkmc.youkaishomecoming.content.pot.rack;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.xkmc.youkaishomecoming.init.data.YHLangData;
import dev.xkmc.youkaishomecoming.init.registrate.YHBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.utility.MathUtils;

import javax.annotation.Nullable;
import java.util.List;

public class DryingRackBlock extends BaseEntityBlock {

	protected static final VoxelShape SHAPE = box(0, 0, 0, 16, 2, 16);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public DryingRackBlock(Properties pProperties) {
		super(pProperties);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
								 InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof DryingRackBlockEntity be) {
			ItemStack stack = player.getItemInHand(hand);
			var opt = be.getCookableRecipe(stack);
			if (opt.isPresent()) {
				if (!level.isClientSide && be.placeFood(player.getAbilities().instabuild ?
						stack.copy() : stack, opt.get().getCookingTime())) {
					return InteractionResult.SUCCESS;
				}
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}

	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		pTooltip.add(YHLangData.DRYING_RACK.get());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			if (level.getBlockEntity(pos) instanceof DryingRackBlockEntity moka) {
				Containers.dropContents(level, pos, moka.getItems());
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}

	}

	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof DryingRackBlockEntity moka) {
			ItemStackHandler inventory = moka.getInventory();
			return MathUtils.calcRedstoneFromItemHandler(inventory);
		}
		return 0;
	}

	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return YHBlocks.RACK_BE.get().create(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
		return level.isClientSide ? null : createTickerHelper(blockEntity, YHBlocks.RACK_BE.get(), DryingRackBlockEntity::cookTick);
	}

	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
	}

	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	public static void buildModel(DataGenContext<Block, DryingRackBlock> ctx, RegistrateBlockstateProvider pvd) {
		var pot = pvd.models().getBuilder("block/drying_rack")
				.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/utensil/drying_rack")))
				.texture("rack", pvd.modLoc("block/utensil/drying_rack"))
				.renderType("cutout");
		pvd.horizontalBlock(ctx.get(), state -> pot);
	}

}

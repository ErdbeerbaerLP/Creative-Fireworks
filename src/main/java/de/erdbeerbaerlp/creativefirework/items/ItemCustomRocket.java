package de.erdbeerbaerlp.creativefirework.items;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import de.erdbeerbaerlp.creativefirework.entity.EntityCustomRocket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFireworkRocket;
import net.minecraft.item.ItemFireworkStar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCustomRocket extends ItemFireworkRocket{

	public ItemCustomRocket(Properties builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Called when this item is used when targetting a Block
	 */
	@Override
	public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
		World world = p_195939_1_.getWorld();
		if (!world.isRemote) {
			BlockPos blockpos = p_195939_1_.getPos();
			ItemStack itemstack = p_195939_1_.getItem();
			EntityCustomRocket entityfireworkrocket = new EntityCustomRocket(world, (double)((float)blockpos.getX() + p_195939_1_.getHitX()), (double)((float)blockpos.getY() + p_195939_1_.getHitY()), (double)((float)blockpos.getZ() + p_195939_1_.getHitZ()), itemstack);
			world.spawnEntity(entityfireworkrocket);
			itemstack.shrink(1);
		}

		return EnumActionResult.SUCCESS;
	}



	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbttagcompound = stack.getChildTag("Fireworks");
		if (nbttagcompound != null) {
			if (nbttagcompound.contains("Flight", 99)) {
				tooltip.add((new TextComponentTranslation("item.minecraft.firework_rocket.flight")).appendText(" ").appendText(String.valueOf((int)nbttagcompound.getByte("Flight"))).applyTextStyle(TextFormatting.GRAY));
			}

			NBTTagList nbttaglist = nbttagcompound.getList("Explosions", 10);
			if (!nbttaglist.isEmpty()) {
				for(int i = 0; i < nbttaglist.size(); ++i) {
					NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
					List<ITextComponent> list = Lists.newArrayList();
					ItemCustomFireworkStar.func_195967_a(nbttagcompound1, list);
					if (!list.isEmpty()) {
						for(int j = 1; j < list.size(); ++j) {
							list.set(j, (new TextComponentString("  ")).appendSibling(list.get(j)).applyTextStyle(TextFormatting.GRAY));
						}

						tooltip.addAll(list);
					}
				}
			}

		}
	}
	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (playerIn.isElytraFlying()) {
			ItemStack itemstack = playerIn.getHeldItem(handIn);
			if (!worldIn.isRemote) {
				EntityCustomRocket entityfireworkrocket = new EntityCustomRocket(worldIn, itemstack, playerIn);
				worldIn.spawnEntity(entityfireworkrocket);
				if (!playerIn.abilities.isCreativeMode) {
					itemstack.shrink(1);
				}
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		} else {
			return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
		}
	}
	public enum Shape implements IStringSerializable {
		RANDOM(0, "random"),
		SMALL_BALL(1, "small_ball"),
		LARGE_BALL(2, "large_ball"),
		STAR(3, "star"),
		CREEPER(4, "creeper"),
		BURST(5, "burst"),
		TEXT(6, "text");

		private static final Shape[] shapes = Arrays.stream(values()).sorted(Comparator.comparingInt((shape) -> {
			return shape.ID;
		})).toArray((shapeID) -> {
			return new Shape[shapeID];
		});
		private final int ID;
		private final String name;

		private Shape(int ID, String name) {
			this.ID = ID;
			this.name = name;
		}

		public int getID() {
			return this.ID;
		}

		@OnlyIn(Dist.CLIENT)
		public String getName() {
			return this.name;
		}

		@OnlyIn(Dist.CLIENT)
		public static Shape getShape(int index) {
			return index >= 0 && index < shapes.length ? shapes[index] : SMALL_BALL;
		}
	}


}

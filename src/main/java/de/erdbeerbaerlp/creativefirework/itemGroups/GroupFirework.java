package de.erdbeerbaerlp.creativefirework.itemGroups;

import de.erdbeerbaerlp.creativefirework.MainClass;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;

public class GroupFirework extends ItemGroup {

    boolean hasSearchBar = false;

    public GroupFirework(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public boolean hasSearchBar() {
        return this.hasSearchBar;
    }

    @Override
    public void fill(NonNullList<ItemStack> l) {
        super.fill(l);


        l.add(new ItemStack(MainClass.FireworkShooter));
        l.add(getFirework(-99, Shapes.STAR, false, true, new int[]{3887386, 5320730, 2437522, 4312372}, new int[]{11743532, 14188952, 12801229, 15435844}, "§eGround-Star", new String[]{"§eStar-Shaped Explosion", "", "§cWARNING: Explodes on ground"}));
        l.add(getFirework(-99, Shapes.LARGEBALL, false, true, new int[]{3887386}, new int[]{11743532, 15435844, 34345665}, "§eGround-Explosion", new String[]{"§aBig Explosion", "", "§cWARNING: Explodes on ground"}));
        l.add(getFirework(-99, Shapes.SPARKLE, false, true, new int[]{3887386}, new int[]{11743532, 15435844, 343456654}, "§eSmall Ground-Explosion", new String[]{"§eSmall Explosion", "", "§cWARNING: Explodes on ground"}));
        l.add(getFirework(1, Shapes.CREEPER, false, false, new int[]{3887386}, new int[]{11743532, 15435844}, "Big Bang", new String[]{"§2Creeper-like explosion"}));
        l.add(getFirework(3, Shapes.LARGEBALL, false, true, new int[]{2343, 234324234, 234341343, 3424, 234234234, 2342}, new int[]{4534545}, ""));
        l.add(getFirework(2, Shapes.SPARKLE, false, false, new int[]{45453, 345345}, new int[]{43543345}, ""));
        l.add(getFirework(1, Shapes.STAR, true, false, new int[]{786734, 76756}, new int[]{}, ""));
        l.add(getFirework(3, Shapes.BALL, false, true, new int[]{875633, 45663445, 12244}, new int[]{67856754}, ""));
        l.add(getFirework(1, Shapes.STAR, true, false, new int[]{45646456}, new int[]{64534345, 63346346, 346436346, 34664712, 8768978}, ""));
        l.add(getFirework(0, Shapes.SPARKLE, true, true, new int[]{5464565, 456654624, 2344324, 8776, 22135}, new int[]{645654, 456564, 456255, 365636, 564245, 567344}, ""));
        l.add(getFirework(2, Shapes.LARGEBALL, true, false, new int[]{654656546, 567567, 234}, new int[]{534535, 34534}, ""));
        l.add(getFirework(1, Shapes.LARGEBALL, false, false, new int[]{43, 5345334, 556, 764}, new int[]{}, ""));
        l.add(getFirework(3, Shapes.CREEPER, true, true, new int[]{5345, 345435435, 34543543, 543, 54}, new int[]{}, ""));
        l.add(getFirework(2, Shapes.BALL, true, true, new int[]{345}, new int[]{4535345}, ""));
        l.add(getFirework(1, Shapes.STAR, false, true, new int[]{9802736, 32984732}, new int[]{9273457}, ""));
        l.add(getFirework(3, Shapes.CREEPER, false, true, new int[]{23974723, 872134675, 23876532}, new int[]{43243, 23423, 634}, ""));
        l.add(getFirework(0, Shapes.SPARKLE, true, false, new int[]{3454543, 345345, 65634, 124, 4}, new int[]{9, 76844, 21342357, 456}, ""));
        l.add(getFirework(-99, Shapes.SPARKLE, true, false, new int[]{234234, 34324523, 4565462, 11234}, new int[]{45342, 2352}, "", new String[]{"§cWARNING: Explodes on ground"}));
        l.add(getFirework(2, Shapes.LARGEBALL, true, true, new int[]{543535, 235235, 7656, 234234}, new int[]{}, ""));
        l.add(getFirework(3, Shapes.LARGEBALL, false, false, new int[]{364346, 346436, 8767, 234324, 7967}, new int[]{6545654}, ""));

        //l.add(getFirework(1, Shapes., , , new int[] {}, new int[] {}, ""));

        if (l.size() < 36) setNoScrollbar();
        if (l.size() > 50) this.hasSearchBar = true;
    }

    public static ItemStack getFirework(int flight, int shape, boolean trail, boolean flicker, int[] colors, int[] fadeColors, String displayName, String[] lores) {
        if (flight > 3) flight = 3;
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT fw = new CompoundNBT();
        ListNBT explosion = new ListNBT();
        CompoundNBT l = new CompoundNBT();

        l.putInt("Flicker", flicker ? 1 : 0);
        l.putInt("Trail", trail ? 1 : 0);
        l.putInt("Type", shape);
        l.put("Colors", new IntArrayNBT(colors));
        l.put("FadeColors", new IntArrayNBT(fadeColors));
        explosion.add(l);

        fw.put("Explosions", explosion);
        fw.putInt("Flight", flight);
        nbt.put("Fireworks", fw);

        CompoundNBT disp = new CompoundNBT();
        if (!displayName.trim().isEmpty()) disp.putString("Name", "[{\"text\":\"§r" + displayName + "\"}]");
        ListNBT loretag = new ListNBT();
//		System.out.println(lores.length);
        if (lores.length > 0) {

            for (String lore : lores) {
//				System.out.println(lore);
                loretag.add(new StringNBT("{\"text\":\"§r" + lore + "\"}"));
            }
//			System.out.println(loretag);
            disp.put("Lore", loretag);
//			System.out.println(disp);

        }

        nbt.putInt("HideFlags", 63);
        nbt.put("display", disp);
        ItemStack i = new ItemStack(Items.FIREWORK_ROCKET);
        i.setTag(nbt);
//  	System.out.println(nbt);
        return i;
    }

    @Override
    public net.minecraft.util.ResourceLocation getBackgroundImage() {
        return this.hasSearchBar ? (new net.minecraft.util.ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png")) : (new net.minecraft.util.ResourceLocation("textures/gui/container/creative_inventory/tab_items.png"));
    }

    @SuppressWarnings("NoTranslation")
    @Override
    public String getTabLabel() {
        return I18n.format("fireworks.name");
    }

    private ItemStack getFirework(int flight, int shape, boolean trail, boolean flicker, int[] colors, int[] fadeColors, String displayName) {
        return getFirework(flight, shape, trail, flicker, colors, fadeColors, displayName, new String[0]);
    }

    private interface Shapes {
        int BALL = 0;
        int LARGEBALL = 1;
        int STAR = 2;
        int CREEPER = 3;
        int SPARKLE = 4;
    }
}

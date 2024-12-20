package me.davidml16.acubelets.menus.player.gifts;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.davidml16.acubelets.Main;
import me.davidml16.acubelets.menus.player.CubeletsMenu;
import me.davidml16.acubelets.objects.*;
import me.davidml16.acubelets.utils.ItemBuilder;
import me.davidml16.acubelets.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiftMenu extends Menu {

    public GiftMenu(Main main, Player player) {
        super(main, player);
        setSize(6);
    }

    @Override
    public void OnPageOpened(int page) {

        Player player = getOwner();

        GiftGuiSession giftGuiSession = (GiftGuiSession) getAttribute(AttrType.GIFT_GUISESSION_ATTR);

        List<CubeletType> cubeletTypes = getCubeletTypesAvailable(player);

        if(page > 0 && cubeletTypes.size() < (page * getPageSize()) + 1) {
            openPage(getPage() - 1);
            return;
        }

        GUILayout guiLayout = getMain().getLayoutHandler().getLayout("gift");

        Profile profile = getMain().getPlayerDataHandler().getData(player);

        Inventory gui = createInventory(getSize(), translateTitleVariables(guiLayout.getMessage("Title"), cubeletTypes.size()));
        ItemStack edge = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).setName("").toItemStack();

        if (page > 0) {

            int amount = guiLayout.getBoolean("Items.PreviousPage.ShowPageNumber") ? page : 1;

            ItemStack item = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.PreviousPage.Material")).get().parseMaterial(), amount)
                    .setName(guiLayout.getMessage("Items.PreviousPage.Name"))
                    .toItemStack();
            item = NBTEditor.set(item, "previous", NBTEditor.CUSTOM_DATA, "action");

            gui.setItem((getSize() - 10) + guiLayout.getSlot("PreviousPage"), item);

        }

        if (cubeletTypes.size() > (page + 1) * getPageSize()) {

            int amount = guiLayout.getBoolean("Items.NextPage.ShowPageNumber") ? (page + 2) : 1;

            ItemStack item = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.NextPage.Material")).get().parseMaterial(), amount)
                    .setName(guiLayout.getMessage("Items.NextPage.Name"))
                    .toItemStack();
            item = NBTEditor.set(item, "next", NBTEditor.CUSTOM_DATA, "action");

            gui.setItem((getSize() - 10) + guiLayout.getSlot("NextPage"), item);

        }

        if(giftGuiSession.isOpenedByCommand()) {

            ItemStack close = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Close.Material")).get().parseItem())
                    .setName(guiLayout.getMessage("Items.Close.Name"))
                    .setLore(guiLayout.getMessageList("Items.Close.Lore"))
                    .toItemStack();
            close = NBTEditor.set(close, "close", NBTEditor.CUSTOM_DATA, "action");

            gui.setItem((getSize() - 10) + guiLayout.getSlot("Close"), close);

        } else {

            ItemStack back = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Back.Material")).get().parseItem())
                    .setName(guiLayout.getMessage("Items.Back.Name"))
                    .setLore(guiLayout.getMessageList("Items.Back.Lore"))
                    .toItemStack();
            back = NBTEditor.set(back, "back", NBTEditor.CUSTOM_DATA,  NBTEditor.CUSTOM_DATA,"action");

            gui.setItem((getSize() - 10) + guiLayout.getSlot("Back"), back);

        }

        fillTopSide(edge, getSizeRows() - 2);

        if (cubeletTypes.size() > getPageSize()) cubeletTypes = cubeletTypes.subList(page * getPageSize(), Math.min(((page * getPageSize()) + getPageSize()), cubeletTypes.size()));

        if(cubeletTypes.size() > 0) {

            for (CubeletType cubeletType : cubeletTypes) {

                long amount = profile.getCubelets().stream().filter(cubelet -> cubelet.getType().equalsIgnoreCase(cubeletType.getId())).count();

                List<String> lore = new ArrayList<>();
                for (String line : guiLayout.getMessageList("Items.Cubelet.Lore")) {
                    lore.add(Utils.translate(line.replaceAll("%cubelets_available%", String.valueOf(amount))));
                }

                ItemStack item = new ItemBuilder(cubeletType.getIcon().clone())
                        .setName(Utils.translate(guiLayout.getMessage("Items.Cubelet.Name").replace("%cubelet_name%", cubeletType.getName())))
                        .setLore(lore)
                        .toItemStack();

                item = NBTEditor.set(item, "send", NBTEditor.CUSTOM_DATA, "action");
                item = NBTEditor.set(item, cubeletType.getId(), NBTEditor.CUSTOM_DATA, "typeID");

                gui.addItem(item);

            }

        }

        fillTopSide(null, getSizeRows() - 2);

        openInventory();

    }

    @Override
    public void OnMenuClick(InventoryClickEvent event) {

        if (event.getCurrentItem() == null) return;

        Player player = getOwner();
        GiftGuiSession session = (GiftGuiSession) getAttribute(AttrType.GIFT_GUISESSION_ATTR);

        String action = NBTEditor.getString(event.getCurrentItem(), NBTEditor.CUSTOM_DATA, "action");

        if(event.getClick() == ClickType.DOUBLE_CLICK) return;

        if(action == null) return;

        switch (action) {

            case "send":
                String type = NBTEditor.getString(event.getCurrentItem(), NBTEditor.CUSTOM_DATA, "typeID");
                CubeletType cubeletType = getMain().getCubeletTypesHandler().getTypeBydId(type);

                GiftCubeletMenu giftCubeletMenu = new GiftCubeletMenu(getMain(), player);

                session.setCubeletType(cubeletType);

                giftCubeletMenu.setAttribute(AttrType.GIFT_GUISESSION_ATTR, session);
                giftCubeletMenu.open();

                break;

            case "previous":
                previousPage();
                break;

            case "next":
                nextPage();
                break;

            case "close":
                player.closeInventory();
                break;

            case "back":
                new CubeletsMenu(getMain(), player).open();
                break;

        }

    }

    @Override
    public void OnMenuClosed() { }

    private List<CubeletType> getCubeletTypesAvailable(Player player) {

        Profile profile = getMain().getPlayerDataHandler().getData(player);

        List<CubeletType> cubeletTypeList = new ArrayList<>();

        for (CubeletType type : getMain().getCubeletTypesHandler().getTypes().values()) {

            long amount = profile.getCubelets().stream().filter(cubelet -> cubelet.getType().equalsIgnoreCase(type.getId())).count();

            if (amount > 0)
                cubeletTypeList.add(type);

        }

        return cubeletTypeList;

    }


}

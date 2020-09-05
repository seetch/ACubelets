package me.davidml16.acubelets.gui;

import me.davidml16.acubelets.Main;
import me.davidml16.acubelets.interfaces.CubeletDateComparator;
import me.davidml16.acubelets.interfaces.CubeletTypeComparator;
import me.davidml16.acubelets.objects.Cubelet;
import me.davidml16.acubelets.objects.CubeletType;
import me.davidml16.acubelets.objects.GUILayout;
import me.davidml16.acubelets.objects.Profile;
import me.davidml16.acubelets.utils.*;
import me.davidml16.acubelets.utils.TimeAPI.TimeUtils;
import me.davidml16.acubelets.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class Cubelets_GUI implements Listener {

    private HashMap<UUID, GuiSession> opened;
    private Main main;

    private ClickType clickType;

    public Cubelets_GUI(Main main) {
        this.main = main;
        this.opened = new HashMap<>();
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
        this.clickType = ClickType.SHIFT_LEFT;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public void setClickType(String clickType) {
        if(Arrays.asList("LEFT", "RIGHT", "MIDDLE", "SHIFT_LEFT", "SHIFT_RIGHT").contains(clickType))
            this.clickType = ClickType.valueOf(clickType.toUpperCase());
        else
            this.clickType = ClickType.SHIFT_LEFT;
    }

    public HashMap<UUID, GuiSession> getOpened() {
        return opened;
    }

    public void reloadPage(Player p) {
        openPage(p, opened.get(p.getUniqueId()).getPage());
    }

    private void openPage(Player p, int page) {

        Profile profile = main.getPlayerDataHandler().getData(p.getUniqueId());
        List<Cubelet> cubelets = profile.getCubelets();

        GUILayout guiLayout = main.getLayoutHandler().getLayout("opencubelet");

        if(profile.getOrderBy().equalsIgnoreCase("date"))
            cubelets.sort(new CubeletDateComparator());
        else if(profile.getOrderBy().equalsIgnoreCase("type"))
            cubelets.sort(new CubeletTypeComparator());

        int pageSize = getPageSize(guiLayout);

        if(page > 0 && cubelets.size() < (page * pageSize) + 1) {
            openPage(p, page - 1);
            return;
        }

        if (cubelets.size() > pageSize) cubelets = cubelets.subList(page * pageSize, Math.min(((page * pageSize) + pageSize), cubelets.size()));

        int neededSize = getNeededSize(guiLayout, cubelets.size());

        Inventory gui = Bukkit.createInventory(null, neededSize, guiLayout.getMessage("Title"));

        if (page > 0) {
            int amount = guiLayout.getBoolean("Items.PreviousPage.ShowPageNumber") ? page : 1;
            gui.setItem(((neededSize - 10) + guiLayout.getSlot("PreviousPage")), new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.PreviousPage.Material")).get().parseMaterial(), amount)
                    .setName(guiLayout.getMessage("Items.PreviousPage.Name"))
                    .toItemStack());
        }

        if (main.getPlayerDataHandler().getData(p.getUniqueId()).getCubelets().size() > (page + 1) * pageSize) {
            int amount = guiLayout.getBoolean("Items.PreviousPage.ShowPageNumber") ? (page + 2) : 1;
            gui.setItem((neededSize - 10) + guiLayout.getSlot("NextPage"), new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.NextPage.Material")).get().parseMaterial(), amount)
                    .setName(guiLayout.getMessage("Items.NextPage.Name"))
                    .toItemStack());
        }

        ItemStack back = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Close.Material")).get().parseItem())
                .setName(guiLayout.getMessage("Items.Close.Name"))
                .setLore(guiLayout.getMessageList("Items.Close.Lore"))
                .toItemStack();
        gui.setItem((neededSize - 10) + guiLayout.getSlot("Close"), back);


        if(main.isCraftingEnabled()) {
            ItemStack crafting = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Crafting.Material")).get().parseItem())
                    .setName(guiLayout.getMessage("Items.Crafting.Name"))
                    .setLore(guiLayout.getMessageList("Items.Crafting.Lore"))
                    .toItemStack();
            gui.setItem((neededSize - 10) + guiLayout.getSlot("Crafting"), crafting);
        }

        for (int i = 0; i <= (neededSize-10); i++)
            gui.setItem(i, null);

        if(main.getCubeletTypesHandler().getTypes().size() > 1) {
            if (profile.getOrderBy().equalsIgnoreCase("date")) {
                ItemStack orderByDate = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Ordered.Date.Material")).get().parseItem())
                        .setName(guiLayout.getMessage("Items.Ordered.Date.Name"))
                        .setLore(guiLayout.getMessageList("Items.Ordered.Date.Lore"))
                        .toItemStack();
                gui.setItem((neededSize - 10) + guiLayout.getSlot("Ordered"), orderByDate);
            } else if (profile.getOrderBy().equalsIgnoreCase("type")) {
                ItemStack orderByType = new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.Ordered.Type.Material")).get().parseItem())
                        .setName(guiLayout.getMessage("Items.Ordered.Type.Name"))
                        .setLore(guiLayout.getMessageList("Items.Ordered.Type.Lore"))
                        .toItemStack();
                gui.setItem((neededSize - 10) + guiLayout.getSlot("Ordered"), orderByType);
            }
        }

        List<ItemStack> items = new ArrayList<>();

        if(cubelets.size() > 0) {

            for (Cubelet cubelet : cubelets) {
                CubeletType type = main.getCubeletTypesHandler().getTypeBydId(cubelet.getType());

                List<String> lore = new ArrayList<>();

                if (cubelet.getExpire() > System.currentTimeMillis()) {
                    for (String line : type.getLoreAvailable()) {
                        lore.add(Utils.translate(line
                                .replaceAll("%received%", TimeUtils.millisToLongDHMS(System.currentTimeMillis() - cubelet.getReceived())))
                                .replaceAll("%expires%", TimeUtils.millisToLongDHMS(cubelet.getExpire() - System.currentTimeMillis())));
                    }
                } else {
                    for (String line : type.getLoreExpired()) {
                        lore.add(Utils.translate(line
                                .replaceAll("%received%", TimeUtils.millisToLongDHMS(System.currentTimeMillis() - cubelet.getReceived()))));
                    }
                }

                ItemStack item = new ItemBuilder(type.getIcon()).setName(Utils.translate(type.getName())).setLore(lore).toItemStack();
                item = NBTEditor.set(item, cubelet.getUuid().toString(), "cubeletUUID");
                item = NBTEditor.set(item, type.getId(), "typeID");

                gui.addItem(item);
                items.add(item);

            }
        } else {
            int slot = 0;
            if(!guiLayout.getBoolean("Size.Dynamic")) {
                if(guiLayout.getSlot("NoCubelets") <= (neededSize - 10)) {
                    slot = guiLayout.getSlot("NoCubelets");
                }
            }

            gui.setItem(slot, new ItemBuilder(XMaterial.matchXMaterial(guiLayout.getMessage("Items.NoCubelets.Material")).get().parseItem())
                    .setName(guiLayout.getMessage("Items.NoCubelets.Name"))
                    .setLore(guiLayout.getMessageList("Items.NoCubelets.Lore")
                    ).toItemStack());
        }

        p.openInventory(gui);

        opened.put(p.getUniqueId(), new GuiSession(p.getUniqueId(), page, cubelets, items));
    }

    public void open(Player p) {
        p.updateInventory();
        openPage(p, 0);
    }

    private int getNeededSize(GUILayout guiLayout, int cubelets) {

        int finalRows = 0;
        int rows = guiLayout.getInteger("Size.Max-Cubelets-Rows");

        if(rows < 1) rows = 1;
        else if(rows > 5) rows = 5;

        if(guiLayout.getBoolean("Size.Dynamic")) {

            if(rows == 1) {
                finalRows = 1;
            } else if(rows == 2) {
                if(cubelets <= 9) finalRows = 1;
                else finalRows = 2;
            } else if(rows == 3) {
                if(cubelets >= 0 && cubelets <= 9) finalRows = 1;
                else if(cubelets >= 9 && cubelets <= 18) finalRows = 2;
                else finalRows = 3;
            } else if(rows == 4) {
                if(cubelets >= 0 && cubelets <= 9) finalRows = 1;
                else if(cubelets >= 9 && cubelets <= 18) finalRows = 2;
                else if(cubelets >= 18 && cubelets <= 27) finalRows = 3;
                else finalRows = 4;
            } else {
                if(cubelets >= 0 && cubelets <= 9) finalRows = 1;
                else if(cubelets >= 9 && cubelets <= 18) finalRows = 2;
                else if(cubelets >= 18 && cubelets <= 27) finalRows = 3;
                else if(cubelets >= 27 && cubelets <= 36) finalRows = 4;
                else finalRows = 5;
            }

        } else {

            finalRows = rows;

        }

        return (finalRows + 1) * 9;
    }

    private int getPageSize(GUILayout guiLayout) {

        int rows = guiLayout.getInteger("Size.Max-Cubelets-Rows");

        if(rows < 1) rows = 1;
        else if(rows > 5) rows = 5;

        return rows * 9;

    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) throws SQLException {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;

        if (opened.containsKey(p.getUniqueId())) {
            e.setCancelled(true);

            int slot = e.getRawSlot();
            int size = p.getOpenInventory().getTopInventory().getSize();
            GUILayout guiLayout = main.getLayoutHandler().getLayout("opencubelet");

            if (slot == ((size - 10) + guiLayout.getSlot("PreviousPage"))) {
                if(e.getClick() != ClickType.DOUBLE_CLICK)
                    openPage(p, opened.get(p.getUniqueId()).getPage() - 1);
            } else if (slot == ((size - 10) + guiLayout.getSlot("NextPage"))) {
                if(e.getClick() != ClickType.DOUBLE_CLICK)
                    openPage(p, opened.get(p.getUniqueId()).getPage() + 1);
            } else if (slot == ((size - 10) + guiLayout.getSlot("Close"))) {
                p.closeInventory();
            } else if (slot == ((size - 10) + guiLayout.getSlot("Crafting")) && main.isCraftingEnabled()) {
                main.getCraftingGUI().open(p);
            } else if (slot == ((size - 10) + guiLayout.getSlot("Ordered")) && main.getCubeletTypesHandler().getTypes().size() > 1) {
                Profile profile = main.getPlayerDataHandler().getData(p.getUniqueId());
                if(profile.getOrderBy().equalsIgnoreCase("date"))
                    profile.setOrderBy("type");
                else if(profile.getOrderBy().equalsIgnoreCase("type"))
                    profile.setOrderBy("date");
                openPage(p, opened.get(p.getUniqueId()).getPage());
            } else if (slot >= 0 && slot <= (p.getOpenInventory().getTopInventory().getSize() - 10)) {
                if (main.getPlayerDataHandler().getData(p.getUniqueId()).getCubelets().size() > 0) {
                    String cubeletUUID = NBTEditor.getString(e.getCurrentItem(), "cubeletUUID");
                    String typeID = NBTEditor.getString(e.getCurrentItem(), "typeID");
                    CubeletType type = main.getCubeletTypesHandler().getTypeBydId(typeID);

                    if(e.getClick() != clickType) {

                        Profile profile = main.getPlayerDataHandler().getData(p);

                        if(profile.getBoxOpened().isWaiting()) {

                            Optional<Cubelet> cubelet = profile.getCubelets().stream().filter(cbl -> cbl.getUuid().toString().equalsIgnoreCase(cubeletUUID)).findFirst();

                            if (cubelet.isPresent()) {
                                if (cubelet.get().getExpire() > System.currentTimeMillis()) {

                                    if (type.getAllRewards().size() > 0) {
                                        main.getCubeletOpenHandler().openAnimation(p, profile.getBoxOpened(), type);

                                        profile.getCubelets().removeIf(cblt -> cblt.getUuid().toString().equals(cubeletUUID));

                                        main.getDatabaseHandler().removeCubelet(p.getUniqueId(), UUID.fromString(Objects.requireNonNull(cubeletUUID)));

                                        main.getHologramHandler().reloadHolograms(p);

                                        p.closeInventory();
                                    }
                                }
                            }

                        } else {
                            if(profile.getBoxOpened().getPlayerOpening().getUuid() == p.getUniqueId()) {
                                p.sendMessage(main.getLanguageHandler().getMessage("Cubelet.BoxInUse.Me"));
                            } else {
                                p.sendMessage(main.getLanguageHandler().getMessage("Cubelet.BoxInUse.Other").replaceAll("%player%", profile.getBoxOpened().getPlayerOpening().getName()));
                            }
                        }

                    } else if (e.getClick() == clickType) {
                        if(main.isPreviewEnabled()) main.getRewardsPreviewGUI().open(p, typeID);
                    }
                } else {
                    int noCubeletsSlot = 0;

                    if(!guiLayout.getBoolean("Size.Dynamic"))
                        if(guiLayout.getSlot("NoCubelets") <= (getPageSize(guiLayout)))
                            noCubeletsSlot = guiLayout.getSlot("NoCubelets");

                    if (slot == noCubeletsSlot) {
                        p.closeInventory();
                        MessageUtils.sendShopMessage(p);
                    }
                }
            }

            p.updateInventory();
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        opened.remove(p.getUniqueId());
    }

    public static class GuiSession {

        private UUID uuid;
        private int page;
        private List<Cubelet> cubeletsDisplayed;
        private List<ItemStack> itemsDisplayed;

        public GuiSession(UUID uuid, int page, List<Cubelet> cubeletsDisplayed, List<ItemStack> itemsDisplayed) {
            this.uuid = uuid;
            this.page = page;
            this.cubeletsDisplayed = cubeletsDisplayed;
            this.itemsDisplayed = itemsDisplayed;
        }

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<Cubelet> getCubeletsDisplayed() {
            return cubeletsDisplayed;
        }

        public void setCubeletsDisplayed(List<Cubelet> cubeletsDisplayed) { this.cubeletsDisplayed = cubeletsDisplayed; }

        public List<ItemStack> getItemsDisplayed() { return itemsDisplayed; }

        public void setItemsDisplayed(List<ItemStack> itemsDisplayed) { this.itemsDisplayed = itemsDisplayed; }

    }

}

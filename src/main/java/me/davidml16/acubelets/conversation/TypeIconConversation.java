package me.davidml16.acubelets.conversation;

import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import me.davidml16.acubelets.Main;
import me.davidml16.acubelets.menus.admin.type.TypeConfigMenu;
import me.davidml16.acubelets.menus.admin.type.TypeSettingsMenu;
import me.davidml16.acubelets.objects.CubeletType;
import me.davidml16.acubelets.objects.Menu;
import me.davidml16.acubelets.utils.Sounds;
import me.davidml16.acubelets.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class TypeIconConversation implements ConversationAbandonedListener, CommonPrompts {

    private Main main;
    public TypeIconConversation(Main main) {
        this.main = main;
    }

    public Conversation getConversation(Player paramPlayer, CubeletType type) {
        Conversation conversation = (new ConversationFactory(main)).withModality(true).withLocalEcho(false).withFirstPrompt(new RenameMenuOptions()).withTimeout(3600).thatExcludesNonPlayersWithMessage("").addConversationAbandonedListener(this).buildConversation(paramPlayer);
        conversation.getContext().setSessionData("player", paramPlayer);
        conversation.getContext().setSessionData("type", type);
        if(main.getCubeletTypesHandler().getConfig(type.getId()).contains("type.icon.texture"))
            conversation.getContext().setSessionData("texture", main.getCubeletTypesHandler().getConfig(type.getId()).get("type.icon.texture"));
        else
            conversation.getContext().setSessionData("icon", type.getIcon());

        main.getConversationHandler().addConversation(paramPlayer);

        return conversation;
    }

    public Conversation getConversation(Player paramPlayer) { return getConversation(paramPlayer, null); }

    public void conversationAbandoned(ConversationAbandonedEvent paramConversationAbandonedEvent) {}

    public class RenameMenuOptions extends FixedSetPrompt {
        RenameMenuOptions() { super("1", "2", "3", "4", "5", "6", "7"); }

        protected Prompt acceptValidatedInput(ConversationContext param1ConversationContext, String param1String) {
            CubeletType cubeletType = (CubeletType) param1ConversationContext.getSessionData("type");
            Player player = (Player) param1ConversationContext.getSessionData("player");
            switch (param1String) {
                case "1":
                    param1ConversationContext.setSessionData("item", "url");
                    return new MineSkinStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter mineskin direct link, \"cancel\" to return.\n\n ", "texture");

                case "2":
                    param1ConversationContext.setSessionData("method", "base64");
                    return new SkullStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter base64 texture string, \"cancel\" to return.\n\n ", "texture");

                case "3":
                    param1ConversationContext.setSessionData("method", "uuid");
                    return new SkullStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter player uuid, \"cancel\" to return.\n\n ", "texture");

                case "4":
                    param1ConversationContext.setSessionData("method", "name");
                    return new SkullStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter player name, \"cancel\" to return.\n\n ", "texture");

                case "5":
                    param1ConversationContext.setSessionData("method", "url");
                    return new MineSkinStringPrompt(main, this, false, ChatColor.YELLOW + "  Enter mineskin direct link, \"cancel\" to return.\n\n ", "texture");

                case "6":
                    final String method = (String) param1ConversationContext.getSessionData("method");

                    if(!method.equalsIgnoreCase("item")) {
                        final String texture = (String) param1ConversationContext.getSessionData("texture");

                        if (!method.equalsIgnoreCase("url")) {

                            main.getCubeletTypesHandler().getConfig(cubeletType.getId()).set("type.icon.texture", method + ":" + texture);

                            switch (method) {

                                case "base64":
                                    cubeletType.setIcon(XSkull.createItem().profile(new Profileable.StringProfileable(texture, ProfileInputType.BASE64)).apply());
                                    break;

                                case "uuid":
                                    cubeletType.setIcon(XSkull.createItem().profile(new Profileable.UUIDProfileable(UUID.fromString(texture))).apply());
                                    break;

                                case "name":
                                    cubeletType.setIcon(XSkull.createItem().profile(new Profileable.UsernameProfileable(texture)).apply());
                                    break;

                            }

                            main.getCubeletTypesHandler().saveConfig(cubeletType.getId());
                            param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                    + " &aSaved skull texture of cubelet type &e" + cubeletType.getId() + " &awithout errors!"));

                            Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                            main.getMenuHandler().reloadAllMenus(TypeConfigMenu.class);
                            main.getMenuHandler().reloadAllMenus(TypeSettingsMenu.class);

                            TypeSettingsMenu typeSettingsMenu = new TypeSettingsMenu(main, player);
                            typeSettingsMenu.setAttribute(Menu.AttrType.CUSTOM_ID_ATTR, cubeletType.getId());
                            typeSettingsMenu.open();

                        } else {

                            Bukkit.getScheduler().runTaskAsynchronously(main, () -> {

                                DataOutputStream out = null;
                                BufferedReader reader = null;

                                try {

                                    URL target = new URL("https://api.mineskin.org/generate/url");
                                    HttpURLConnection con = (HttpURLConnection) target.openConnection();
                                    con.setRequestMethod("POST");
                                    con.setDoOutput(true);
                                    con.setConnectTimeout(1000);
                                    con.setReadTimeout(30000);
                                    out = new DataOutputStream(con.getOutputStream());
                                    out.writeBytes("url=" + URLEncoder.encode(texture, "UTF-8"));
                                    out.close();
                                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                    JSONObject output = (JSONObject) new JSONParser().parse(reader);
                                    JSONObject data = (JSONObject) output.get("data");
                                    JSONObject texture1 = (JSONObject) data.get("texture");
                                    String textureEncoded = (String) texture1.get("value");
                                    con.disconnect();

                                    Bukkit.getScheduler().runTask(main, () -> {

                                        main.getCubeletTypesHandler().getConfig(cubeletType.getId()).set("type.icon.texture", "base64:" + textureEncoded);
                                        cubeletType.setIcon(XSkull.createItem().profile(new Profileable.StringProfileable(textureEncoded, ProfileInputType.BASE64)).apply());

                                        main.getCubeletTypesHandler().saveConfig(cubeletType.getId());
                                        param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                                + " &aSaved skull texture of cubelet type &e" + cubeletType.getId() + " &awithout errors!"));

                                        Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                                        main.getMenuHandler().reloadAllMenus(TypeConfigMenu.class);
                                        main.getMenuHandler().reloadAllMenus(TypeSettingsMenu.class);

                                        TypeSettingsMenu typeSettingsMenu = new TypeSettingsMenu(main, player);
                                        typeSettingsMenu.setAttribute(Menu.AttrType.CUSTOM_ID_ATTR, cubeletType.getId());
                                        typeSettingsMenu.open();

                                    });

                                } catch (Throwable t) {
                                    t.printStackTrace();
                                } finally {
                                    if (out != null) {
                                        try {
                                            out.close();
                                        } catch (IOException e) {
                                        }
                                    }
                                    if (reader != null) {
                                        try {
                                            reader.close();
                                        } catch (IOException e) {
                                        }
                                    }
                                }

                            });

                        }
                    } else {
                        final ItemStack icon = (ItemStack) param1ConversationContext.getSessionData("icon");

                        XItemStack.serialize(icon, Utils.getConfigurationSection(main.getCubeletTypesHandler().getConfig(cubeletType.getId()), "type.icon"));

                        main.getCubeletTypesHandler().saveConfig(cubeletType.getId());
                        param1ConversationContext.getForWhom().sendRawMessage("\n" + Utils.translate(main.getLanguageHandler().getPrefix()
                                + " &aSaved icon of cubelet type &e" + cubeletType.getId() + " &awithout errors!"));

                        Sounds.playSound(player, player.getLocation(), Sounds.MySound.ANVIL_USE, 10, 3);

                        main.getMenuHandler().reloadAllMenus(TypeConfigMenu.class);
                        main.getMenuHandler().reloadAllMenus(TypeSettingsMenu.class);

                        TypeSettingsMenu typeSettingsMenu = new TypeSettingsMenu(main, player);
                        typeSettingsMenu.setAttribute(Menu.AttrType.CUSTOM_ID_ATTR, cubeletType.getId());
                        typeSettingsMenu.open();
                    }

                    main.getConversationHandler().removeConversation(player);

                    return Prompt.END_OF_CONVERSATION;

                case "7":
                    return new CommonPrompts.ConfirmExitPrompt(main, this);

            }
            return null;
        }


        public String getPromptText(ConversationContext param1ConversationContext) {
            String cadena = "";
            cadena += ChatColor.GOLD + "" + ChatColor.BOLD + "\n  CUBELET TYPE ICON MENU\n ";
            cadena += ChatColor.GREEN + " \n ";
            cadena += ChatColor.GREEN + "    1 " + ChatColor.GRAY + "- Item in hand\n ";
            cadena += ChatColor.GREEN + "    2 " + ChatColor.GRAY + "- Base64 String\n ";
            cadena += ChatColor.GREEN + "    3 " + ChatColor.GRAY + "- Player UUID\n ";
            cadena += ChatColor.GREEN + "    4 " + ChatColor.GRAY + "- Player Name\n ";
            cadena += ChatColor.GREEN + "    5 " + ChatColor.GRAY + "- Mineskin Direct Link\n ";
            cadena += ChatColor.GREEN + " \n ";
            cadena += ChatColor.GOLD + "    6 " + ChatColor.GRAY + "- Save and exit\n ";
            cadena += ChatColor.GOLD + "    7 " + ChatColor.GRAY + "- Exit and discard\n ";
            cadena += ChatColor.GREEN + "\n ";
            cadena += ChatColor.GOLD + "" + ChatColor.YELLOW + "  Choose the option: \n ";
            return cadena;
        }
    }

}
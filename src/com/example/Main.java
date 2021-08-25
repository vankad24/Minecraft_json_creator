package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner in = new Scanner(System.in);
    static String path, modid, name;
    static File preparedFiles, assets, data;
    static boolean isBlock;

    String version = "1.15.2";
    public static void main(String[] args) {

        path = "G:\\Projects\\ideaProjects\\Magnum-magic\\src\\main\\resources";
        modid = "magmagic";
        name = "magic_stone_block";





        path = path.replace("\\","/").trim();
        modid = modid.trim();
        name = name.trim();
        if (!path.isEmpty())System.out.println(path);
        File resources = new File(path);
        while (path.isEmpty()||!resources.exists()) {
            path = inputRequest("the path to your res directory \n(like C:/Projects/MyMode/src/main/resources)")
                    .replace("\\","/").trim();
            resources = new File(path);
        }



        if (modid.isEmpty())modid = inputRequest("your modid");
        if (name.isEmpty())name = inputRequest("the name of your thing");


        if (name.contains("item"))isBlock= !ask("It is an item");
        else isBlock = ask("It is a block");

        assets = new File(resources,"assets/"+modid);
        data = new File(resources,"data/"+modid);
        preparedFiles = new File("files");

        if (isBlock&&wantAsk("blockstate"))createBlockstate();
        if (wantAsk("model"))createModel();
        if (isBlock&&wantAsk("loot table"))createLootTable();
        if (wantAsk("recipe"))createRecipe();

        new File(assets,"lang").mkdir();
        System.out.println("And add to your lang file");
        System.out.println((isBlock?"\"block.":"\"item.")+modid+"."+name+"\":\""+name.replace("_"," ")+"\"");
        //        "block.magmagic.lampblock": "Lamp Block"
        System.out.println("Good luck!");
    }

    private static void createRecipe() {
        String text;
        if (!ask("The recipe is shaped")){
            text =  prepareFile("recipe_shapeless");
            String item = "{\n" +
                    "      \"item\": \"thing\"\n" +
                    "    }";
            System.out.println("How many items do you use?");
            int quantity = in.nextInt();
            String items = "";
            System.out.println("Please, type full name for each item. Example:'minecraft:stone'");
            for (int i = 1; i <= quantity; i++) {
                System.out.print(i+":");
                items+=item.replace("thing",in.next());
                if (i!=quantity)items+=",\n";
            }
            text = text.replace("items",items);
        }else {
            text = prepareFile("recipe");
            System.out.println("Type pattern for the recipe. For an empty line, enter 'e'. Example:\n###\n# *\ne");
            StringBuilder pattern = new StringBuilder();
            ArrayList<Character> chars = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String s = in.next();
                for (int j = 0; j < s.length(); j++) {
                    char c = s.charAt(j);
                    if (c != 'e' && c != ' ' && !chars.contains(c)) chars.add(c);
                }
                if (!s.contains("e")) {
                    pattern.append("\"").append(s).append("\"").append(",\n");
                }
            }

            text = text.replace("ready_pattern", pattern.substring(0, pattern.length() - 2) + "\n");

            String key = "\"key\": {\n" +
                    "      \"item\": \"thing\"\n" +
                    "    }";
            String keys = "";
            for (int i = 0; i < chars.size(); i++) {
                System.out.println("Type full name of item for the key. Example:'# is minecraft:stone'");
                System.out.print(chars.get(i) + " is ");
                keys += key.replace("key", chars.get(i) + "").replace("thing", in.next());
                if (i != chars.size() - 1) keys += ",\n";
            }
            text = text.replace("ready_keys", keys);
        }
        createReadyFile(text,new File(data,"recipes"));
    }

    private static void createLootTable() {
        File loot_table = new File(data,"loot_tables/blocks");
        String text = prepareFile("loot_table_block");
        if (!ask("Do you want to drop the same block")){
            System.out.print("Please, type modid:name_of_drop");
            text = text.replace(modid+":"+name,in.next());
        }
        createReadyFile(text,loot_table);
    }

    private static void createModel() {
        File model = new File(assets,"models/item");
        if (isBlock){
            createReadyFile(prepareFile("model_block_item"),model);
            createReadyFile(prepareFile("model_block"),new File(model.getParentFile(),"block"));
        }else {
            createReadyFile(prepareFile("model_item"),model);
        }

        //create folders for textures
        File textures = new File(assets,"textures");
        new File(textures,"block").mkdirs();
        new File(textures,"item").mkdirs();

    }

    static void createBlockstate(){
        createReadyFile(prepareFile("blockstates"),new File(assets,"blockstates"));
    }

    static String prepareFile(String copiedFile){
        String text="";
        try {
            File file = new File(preparedFiles,copiedFile+".json");
            char[] str = new char[(int) file.length()];
            FileReader reader = new FileReader(file);
            reader.read(str);
            text = new String(str).replaceFirst("modid",modid).replaceFirst("thing",name);
            reader.close();
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
        return text;
    }

    static void createReadyFile(String fileText, File to){
        to.mkdirs();
        try {
            FileWriter writer = new FileWriter(new File(to,name+".json"));
            writer.write(fileText);
            writer.close();
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    static String inputRequest(String request){
        System.out.print("Please, type "+request+":");
        return in.next();
    }

    static boolean ask(String question){
        String ans;
        do {
            System.out.println(question+"?");
            ans = in.next().toLowerCase();
        }while (!ans.equals("y") && !ans.equals("yes") && !ans.equals("n") && !ans.equals("no"));
        return ans.startsWith("y");
    }

    static boolean wantAsk(String question){
        return ask("Do you want to create "+question);
    }
}

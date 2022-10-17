package com.andrey.guryanov.harmonica;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrey.guryanov.harmonica.utils.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class FileListControl extends FileList {
    /** ТуДу
        -Итему надо присвоить ИсФайл
        -Менять иконку кнопки Назад, если пользователь находится не в домашней папке
        -выводить в текстВью Консоль свойства текущей открытой папки (и пока что файла)
        -менять иконку папки в списке, если элемент - файл
        -привязать чекбокс к элементу списка
        -сортировать элементы перед показом
        -показывать сначала сортированные по имени папки, потом так же файлы
        -отображать в списке только папки и музыкальные файлы
     -получить разрешение на доступ к файлам внутренней памяти, к флешке (начиная с 11 андроида)
        -обработать выбор чекбокса на папке
        -сделать метод по формированию плейлиста на основе выбранных файлов
        -обработать кнопку Выбрать всё
        -переопределить стандартную кнопку Назад
        -получить путь к корневой папке приложения для сериализации
     -сделать создание начальных папок разными для разных версий андроида (11+, и 10-) и сохранять их
     -добавить кнопку Добавить папку в ФайлЛист и сохранять ее вместе с остальными домашними папками
     -после заполнения плейлиста, его надо сохранить (сериализировать). Создавать плейлист (дефолтный) нужно при первом запуске приложения

     -
     -
     **/


    /** БАГИ
     -при переходах по папкам вперед-назад наблюдается произвольное название имени и пути текущей папки
        -при нажатии на файл выделяется чекбокс, и он присваивается не элементу, а вьюХолдеру, и при пролистывании списка переобновляемые вьюхолдеры также выделены
     -иконка файла присваевается ВьюХолдеру, и при пролистывании списка обратно не ставит иконку папки

     -
     -
    **/


    /** ОПТИМИЗАЦИЯ
     -вынести все чекбоксы в отдельный массив для более удобного управления, а также оптимизации программы в плане работы с оными
     -плейлист формировать не как объект с объектами Song, а как текстовый файл.

     -
     -
     **/
    private LayoutInflater inflater;
    private Context parent;
    private RecyclerView fileList;
//    private View parentView;
    private TextView folderName, folderPath, console;
    private ImageButton goBack;

    //private final Context parentCon = getActivityContext();//test

    //private FileListAdapter currentAdapter;
//    private List<Item> homeFolders;
//    private List<TextView> textArgs;
    private List<Item> folderItems;
    private List<List<Item>> listFolder;
    private String currentPath, currentFolder;
    private PlayList playList;
//    private String[] current;
    private List<String[]> navigation;
    private int count;  //счетчик шагов, которые были сделаны от начальных папок (домашних), до
    // конечного пути, до которого дойдет пользователь. Нужен для того, чтобы не получать каждый раз
    // задние пути, сканируя папки на телефоне, когда пользователь решит вернуться к предыдущей
    // папке. Все пути, которые пройдет пользователь в одном направлении, сохраняются в "Список
    // списков" (listFolder), а счетчик служит индексом, чтобы нужные пути из "Списка списков" можно
    // было быстренько получить.


    public FileListControl() {

    }

    public FileListControl(Context parent, RecyclerView fileList, List<Object> args) {
        this.parent = parent;
        this.inflater = LayoutInflater.from(parent);
        this.fileList = fileList;

        this.folderName = (TextView) args.get(0);
        this.folderPath = (TextView) args.get(1);

        this.console = (TextView) args.get(2);
        this.goBack = (ImageButton) args.get(3);

        this.playList = App.getPlayer().getCurrentPlayList();
//        this.parentView = view;

    }

//    public void refreshFileList(RecyclerView fileList, List<Item> paths) {
//        FileListAdapter adapter = new FileListAdapter(context, paths);
//        fileList.setAdapter(adapter);
//    }

    public void saveHomeFolders() {

    }

    public void createHomeFolders() { //Получаем первые пути
        //Получаем путь до внутреннего накопителя
        String internal = Environment.getExternalStorageDirectory().getPath();
//        String SDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();
        //Получаем путь до системной папки
        String system = Environment.getRootDirectory().getPath();
        //Получаем путь до карты памяти
        String SDCard = internal.replaceFirst("0", "1");
        String musicFolder = "/music";

        //File file = new File(this.getFilesDir(),"/");
        String data = parent.getFilesDir().getPath();
        //String appData = getApplicationInfo().dataDir;
        String appData = App.getPlayListsDir();

//        navigation.add("home");
        folderItems = new ArrayList<>();
        folderItems.add(new Item(SDCard, "SD карта"));
        folderItems.add(new Item(SDCard + musicFolder, "SD карта - Музыка"));
        folderItems.add(new Item(internal, "Внутр. память"));
        folderItems.add(new Item(internal + musicFolder, "Внутр. память - Музыка"));

        folderItems.add(new Item(system, "Системная папка"));
        folderItems.add(new Item("/", "Test Folder"));//

        folderItems.add(new Item(data, "Папка с данными приложения"));

        folderItems.add(new Item(appData, "Папка с какой-то музыкой"));

        listFolder.add(folderItems);    //добавляем домашние папки в начало "Списка папок"
//        current = new String[] {"Домашняя папка", " "};
        navigation = new ArrayList<>();
//        navigation.add(current);
        navigation.add(new String[] {"Домашняя папка", " "});
    }

    public List<Item> getHomeFolders() {
        count = 0;
        listFolder = new ArrayList<>();
//        if (listFolder.size() != 0) return listFolder.get(0); //
//        else createHomeFolders(); //
        createHomeFolders();
        return listFolder.get(0);
    }

//    public static void nextNavigation(String path) {
//        navigation.add(path);
//    }

//    public static void prevNavigation() {
//        int lastIndex = navigation.size() - 1;
//        navigation.remove(lastIndex);
//    }

//    public void replaceText


//    public void goToNext(String path) {
//        count++;
//
//    }



//    public static String getCurrentPath() {
//        int lastIndex = navigation.size() - 1;
//        return navigation.get(lastIndex);
//    }

//    public void initRecyclerView() {
//        fileList = view.findViewById(R.id.rv_file_list);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parent);
//        fileList.setLayoutManager(layoutManager);
//        FileListAdapter adapter = new FileListAdapter(parent, view, fileList, FileList.getFirstPaths());
//        fileList.setAdapter(adapter);
//
//        currentAdapter = adapter;
//    }

    public void setText() {

//        folderName = (TextView) findViewById(R.id.tv_folder_name);
//        folderName.setText(currentFolder);
//        folderPath = findViewById(R.id.tv_folder_path);
//        getFolderName().setText(currentFolder);
//        folderPath.setText(currentPath);
//        setCurrentFolderName(currentFolder, currentPath);
//        List<TextView> text = super.getText();
//        TextView name = text.get(0);
//        name.setText(currentFolder);
        folderName.setText(currentFolder);  //возможно баг с отображением имени папки кроется здесь
        folderPath.setText(currentPath);

    }

    public void refreshNaviItem() {
//        current[0] = currentFolder;
//        current[1] = currentPath;
        navigation.add(new String[] {currentFolder, currentPath});
    }

//    public void testConsole(Item item) {
//        StringBuilder builder = new StringBuilder();
//        String n = "\n";
//        builder.append("Имя: " + item.getName() + n)
//                .append("Путь: " + item.getPath() + n)
//                .append("Это файл? " + item.isFile() + n)
//                .append("Расширение: " + item.getExtension());
//        console.setText(builder.toString());
//    }

    public void goToNext(int position) {
        Item item = folderItems.get(position);
        //testConsole(item);  //вывод свойств открываемой папки в тестовую консоль
        currentPath = item.getPath();
        currentFolder = item.getName();
        setText();
        refreshNaviItem();
        folderItems = new ArrayList<>();
        folderItems = getNewItemsFrom(currentPath);
        listFolder.add(folderItems);
        count++;

        goBack.setImageResource(android.R.drawable.ic_menu_revert); //при переходе в след. папку каждый раз задаём кнопке Назад иконку реверса
        startAdapter(folderItems);
    }

    public void goToBack() {
        listFolder.remove(count);
        navigation.remove(count);
        count--;
        currentFolder = navigation.get(count)[0];
        currentPath = navigation.get(count)[1];
        setText();
        refreshNaviItem();
        folderItems = new ArrayList<>();
        folderItems = listFolder.get(count);

        if (count == 0) goBack.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); //если возвращаемся в Домашнюю папку, кнопка Назад становится крестиком
        startAdapter(folderItems);
    }

    public void startAdapter(List<Item> folderItems) {
        FileListAdapter adapter = new FileListAdapter(parent, this, folderItems);
        //currentAdapter = adapter;
        fileList.setAdapter(adapter);

//        ItemListFragment.setCurrentAdapter(adapter);
    }

//    public void clickOnViewHolder(int position) {
//        if (folderItems.get(position).getIsFile()) {
//            Item item = folderItems.get(position);
//            currentAdapter.ViewHolder checkBox.setChecked(item.replaceFlag());
//        }
//        else {
//            for(Item item : folderItems) {
//                item.setFlag(false);
//            }
//            goToNext(position);
//        }
//    }

    public void checkAll() {
        int tempCount = 0;
        int size = folderItems.size();
        for (Item item : folderItems) { //1 цикл (проверяющий)
            if (item.getFlag()) tempCount++;
        }
        if (tempCount == 0 || tempCount == size) {
            for (Item item : folderItems) {
                item.replaceFlag();
            }
        }
        else {
            for (Item item : folderItems) {
                item.setFlag(true);
            }
        }
        startAdapter(folderItems);
    }

    public int getCount() {
        return count;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

//    public void createNewItem() {
//        Item item = new Item();
//    }

    public String getFileExtension(File element) {
        String fileName = element.getName();
        int index = fileName.lastIndexOf(".");
        if (index > 0) return fileName.substring(index + 1);
        else return "null";
    }

    public List<Item> getNewItemsFrom(String path) {

//        try (FileReader reader = new FileReader(path);
//            Scanner scan = new Scanner(reader)) {
//
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }




        File file = new File(path);

//        String[] paths = file.list();
//        if (paths == null) {return null;}
//        sort(paths);

        File[] files = file.listFiles();
        sort(files);
        //File[] files = convertToFileArray(paths);//new File[paths.length];

        List<Item> itemArray = new ArrayList<>();
        List<Item> folderArray= new ArrayList<>();
        if (files != null) {
            for (File element : files) {
                if (element.isFile()) {
                    String ext = getFileExtension(element);
                    if (ext.equalsIgnoreCase("mp3")) {
                        itemArray.add(new Item(true, element.toString(), ext));
                    }
                }
                else {
                    folderArray.add(new Item(element.toString()));
                }

            }
//            for (int i = 0; i < files.length; i++) {
//                Item newItem = new Item(files[i].toString());
//                if (files[i].isFile()) {
//                    newItem.setIsFile(true);
//
//
//                }
//            itemList.add(newItem);  /** здесь куча проблем, надо через потоки получать список элементов директории */
//            }

        }
        else {
//            Toast toast = Toast.makeText(parent, "Папка пуста", Toast.LENGTH_SHORT);
//            toast.show();
            //folderArray.add(new Item(path, "Тут пусто, увы :("));
            return folderArray;
        }

        return joinLists(folderArray, itemArray);//itemList;
    }

    public List<Item> joinLists(List<Item> list1, List<Item> list2) {
        for (Item item : list2) {
            list1.add(item);
        }
        return list1;
    }

    public void sort(File[] pathsArray) {  //массив может быть нулл, надо это обработать
        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getPath().compareToIgnoreCase(o2.getPath());
            }
        };
//        String[] sortArray = new String[pathsArray.length];
//        for (int i = 0; i < array.size(); i++) {
//            sortArray[i] = array.get(i).getName();
//        }

//        if (pathsArray == null) { //
//            Toast toast = Toast.makeText(parent, "Такой папки не существует!", Toast.LENGTH_SHORT);
//            toast.show();
//        }
        //else
        Arrays.sort(pathsArray, comparator);
    }


    public void findFilesInFolder(Item item) {
        List<Item> tempList = new ArrayList<>(getNewItemsFrom(item.getPath())); //можно не создавать список, а сразу его использовать
        for (Item tempItem : tempList) {
            addSongs(tempItem);
        }
    }

    public void fillPlayList() {

        for (Item item : folderItems) {
            if (item.getFlag()) {
                addSongs(item);
            }
        }

        //super.onBackPressed();


        if (playList.getCountSongs() == 0) {
            Toast toast = Toast.makeText(inflater.getContext(), "Музыка не найдена!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(inflater.getContext(), "Музыка успешно добавлена!", Toast.LENGTH_SHORT);
            toast.show();
            //тут надо добавить сравнение, чтобы не допустить одинаковых песен
            App.getPlayer().savePlayLists();
        }

    }

    public void addSongs(Item item) {

        if (item.isFile()) playList.addSong(item.getName(), item.getPath());
        else {
            findFilesInFolder(item);

        }
    }

    public void serializePlayList() {

    }

//    public File[] convertToFileArray(String[] paths) {
//        File[] fileArray = new File[paths.length];
//        for (int i = 0; i < paths.length; i++) {
//            fileArray[i] = new File(paths[i]);
//        }
//        return fileArray;
//    }


}

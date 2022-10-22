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
     -плейлист формировать не как объект с объектами Track, а как текстовый файл.

     -
     -
     **/


    private final LayoutInflater inflater;
    private final Context parent;
    private final RecyclerView fileList;
    private TextView folderName, folderPath;
    private ImageButton goBack;

    //private FileListAdapter currentAdapter;
    private List<Item> homeFolders;
    private List<Item> folderItems;                                                                 //список элементов (имена и пути папок и файлов), находящихся в текущей директории
    private List<List<Item>> listFolder;                                                            //список, содержащий в себе списки folderItems. Нужен для того, чтобы не обращаться к
                                                                                                    //-- памяти всякий раз, когда возвращаемся в предыдущую папку
    private String currentPath, currentFolder;                                                      //Строки, хранящие в себе путь и имя текущей папки
    private PlayList playList;                                                                      //тут храним ссылку на объект плейлиста, в который будем добавлять треки
//    private String[] current;
    private List<String[]> navigation;                                                              //список, содержащий в себе массив из строк с путем и именем текущей папки. Нужен для
                                                                                                    //-- хранения и отображения пути и имени текущей папки, когда возвращаемся на папку выше
    private int count;                                                                              //счетчик шагов, которые были сделаны от начальных папок (домашних), до
                                                                                                    //-- конечного пути, до которого дойдет пользователь. Нужен для того, чтобы не получать
                                                                                                    //-- каждый раз задние пути, сканируя папки на телефоне, когда пользователь решит
                                                                                                    //-- вернуться к предыдущей папке. Все пути, которые пройдет пользователь в одном
                                                                                                    //-- направлении, сохраняются в "Список списков" (listFolder), а счетчик служит
                                                                                                    //-- индексом, чтобы нужные пути из "Списка списков" можно было быстренько получить.


    public FileListControl(Context parent, RecyclerView fileList, List<Object> args) {
        this.parent = parent;
        this.inflater = LayoutInflater.from(parent);
        this.fileList = fileList;

        this.folderName = (TextView) args.get(0);
        this.folderPath = (TextView) args.get(1);
        this.goBack = (ImageButton) args.get(2);

        this.playList = App.getPlayer().getCurrentPlayList();
    }


    /** ТОЧКИ ВЗАИМОДЕЙСТВИЯ (ИНТЕРФЕЙС ПОЛЬЗОВАТЕЛЯ) */


    public void showHomeFolders() {                                                                 //метод отображает список домашних папок
        startAdapter(initHomeFolders());
    }

    public void openFolder(int position) {                                                          //метод выполняет переход по кликнутой папке
        Item item = folderItems.get(position);
        currentPath = item.getPath();
        currentFolder = item.getName();
        setText();
        refreshNaviItem();
        folderItems = new ArrayList<>(getNewItemsFrom(currentPath));
//        folderItems = getNewItemsFrom(currentPath);
        listFolder.add(folderItems);
        count++;

        /** $
        // нужен отдельный метод для замены иконки */
        goBack.setImageResource(android.R.drawable.ic_menu_revert);                                 //при переходе в след. папку каждый раз задаём кнопке Назад иконку реверса
        startAdapter(folderItems);
    }

    public void returnToPrevFolder() {                                                              //метод для возвращения в предыдущую папку
        listFolder.remove(count);
        navigation.remove(count);
        count--;
        currentFolder = navigation.get(count)[0];
        currentPath = navigation.get(count)[1];
        setText();
        refreshNaviItem();
        folderItems = new ArrayList<>(listFolder.get(count));
//        folderItems = listFolder.get(count);

        if (count == 0) goBack.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);     //если возвращаемся в Домашнюю папку, кнопка Назад становится крестиком
        startAdapter(folderItems);
    }

            /** $
    // тут несколько циклов, можно это оптимизировать */
    public void selectAll() {                                                                       //метод выбирает все файлы и папки в текущей папке
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
        startAdapter(folderItems);                                                                  // ! //костыль
    }

            /** ! */
    public void fillPlayList() {                                                                    //метод добавляет в плейлист выбранные треки
        for (Item item : folderItems) {
            if (item.getFlag()) {
                addTracks(item);
            }
        }
            /** !
        // тут кроется проблема, что если плейлист уже содержит треки, то всегда будет выводиться, что музыка успешно добавлена */
        if (playList.getCountTracks() == 0) {                                                       //если ни одного трека не было добавлено, то выводим сообщение "Музыка не найдена!"
            Toast toast = Toast.makeText(inflater.getContext(), "Музыка не найдена!"
                    , Toast.LENGTH_SHORT);
            toast.show();
        }
        else {                                                                                      //иначе, если хоть один трек был добавлен, то выводим сообщение об успехе операции
            Toast toast = Toast.makeText(inflater.getContext(), "Музыка успешно добавлена!"
                    , Toast.LENGTH_SHORT);
            toast.show();
                /** !
            // тут надо добавить сравнение, чтобы не допустить одинаковых песен */
            App.getPlayer().savePlayLists();                                                        //после добавления трека/ов сохраняем (перезаписываем) плейлист
        }

    }


    /** РАБОТА С ДОМАШНИМИ (НАЧАЛЬНЫМИ) ПАПКАМИ */


            /** !
    // метод функционирует лишь частично! */
    private void createHomeFolders() {                                                              //метод создаёт домашние (начальные) папки, если это первый запуск приложения
        //Получаем путь до внутреннего накопителя
        String internal = Environment.getExternalStorageDirectory().getPath();
//        String SDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
//              .toString();
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

    private void loadHomeFolders() {

    }

    private void saveHomeFolders() {

    }


    /** ПОИСК И ДОБАВЛЕНИЕ ТРЕКОВ В ПЛЕЙЛИСТ */


    private void findFilesInFolder(Item item) {                                                     //метод принимает объект Итем (который является папкой), и ищет в его директории файлы
        List<Item> tempList = new ArrayList<>(getNewItemsFrom(item.getPath()));                     //создаётся список, содержащий в себе файлы и папки, находящиеся в директории Итема
        for (Item tempItem : tempList) {                                                            //цикл пробегает по списку и пытается добавить каждый Итем в плейлист
            addTracks(tempItem);                                                                    //попытка преобразовать Итем в объект Трек и добавить его в плейлист
        }
    }

    private void addTracks(Item item) {                                                             //метод добавляет в плейлист Трек на основе Итема, если Итем является муз. файлом
        if (item.isFile()) playList.addTrack(item.getName(), item.getPath());                       //если Итем является файлом, то добавляем трек в плейлист на основе данных Итема
        else {                                                                                      //иначе (если Итем является папкой)
            findFilesInFolder(item);                                                                //вызываем метод поиска файлов и передаём туда директорию Итема (Итем же у нас папка)
        }
    }


    /** ГЕТТЕРЫ */


    public int getCount() {                                                                         //метод возвращает кол-во открытых папок по одному пути относительно домашней папки
        return count;
    }


    /** СЛУЖЕБНЫЕ МЕТОДЫ */


    private List<Item> initHomeFolders() {                                                          //метод инициализирует домашние папки. Если это первый запуск приложения, то метод
                                                                                                    //-- создаёт домашние папки, если они уже были созданы ранее- загружает их
        count = 0;
        listFolder = new ArrayList<>();
//        if (listFolder.size() != 0) return listFolder.get(0); //
//        else createHomeFolders(); //
        createHomeFolders();
        return listFolder.get(0);
    }

            /** !
    // возможно баг с отображением имени папки кроется здесь */
    private void setText() {                                                                        //метод меняет значение текущей папки и её пути, где находится пользователь
        folderName.setText(currentFolder);
        folderPath.setText(currentPath);
    }

            /** $
    // можно хранить не массивы строк, а int переменные - указатели, в каком месте списка папок находится Итем с именем и путём текущей папки */
    private void refreshNaviItem() {                                                                //метод сохраняет имя и путь текущей папки перед переходом в следующую папку
        navigation.add(new String[] {currentFolder, currentPath});
    }

    private void startAdapter(List<Item> folderItems) {                                             //запускает отрисовку ресайклерВью для отображения папок и файлов
        FileListAdapter adapter = new FileListAdapter(parent, this, folderItems);
        fileList.setAdapter(adapter);
    }

    private String getFileExtension(File element) {                                                 //метод возвращает расширение принимаемого Файла
        String fileName = element.getName();                                                        //создаётся результирующая строка, которая хранит в себе имя принятого Файла
        int index = fileName.lastIndexOf(".");                                                      //ищётся индекс символа точки (.) в имени Файла, которая находится первой с конца
        if (index > 0) return fileName.substring(index + 1);                                        //если точка найдена, возвращаем всё, что находится после точки
        else return "null";                                                                         //иначе возвращаем строку "null", то есть Файл у нас без расширения
    }

            /** !
    // проблема с вылетом NPE при переходе в пустую/несуществующую папку кроется здесь */
    private List<Item> getNewItemsFrom(String path) {                                               //метод возвращает список элементов (папок и файлов), находящихся по переданному пути
        File[] files = new File(path).listFiles();                                                  //создаётся массив, который получает все элементы из переданной директории как файлы
        sort(files);                                                                                //сортировка элементов директории (файлов) по алфавиту

        List<Item> itemArray = new ArrayList<>();                                                   //создаётся список для хранения только музыкальных файлов (на данный момент только mp3)
        List<Item> folderArray= new ArrayList<>();                                                  //создаётся список для хранения только папок
        if (files != null) {                                                                        //если созданный массив не пустой (т.е. в директории есть хотя бы 1 файл или папка)
            for (File element : files) {                                                            //пробегаемся циклом по массиву и отделяем папки от файлов
                if (element.isFile()) {                                                             //если элемент массива является файлом
                    String ext = getFileExtension(element);                                         //тогда получаем у этого файла его расширение
                    if (ext.equalsIgnoreCase("mp3")) {                                              //если расширение "mp3", то есть это звуковой файл
                        itemArray.add(new Item(true, element.toString(), ext));                     //создаём объект Итем на базе этого файла, маркируем его как файл и добавляем его в
                    }                                                                               //-- список файлов
                }
                else {                                                                              //если элемент является папкой
                    folderArray.add(new Item(element.toString()));                                  //то так же создаём объект Итем на базе элемента и добавляем его в список папок
                }
            }
        }
        else {                                                                                      //иначе, если массив элементов пустой (директория пуста)
//            Toast toast = Toast.makeText(parent, "Папка пуста", Toast.LENGTH_SHORT);
//            toast.show();
            //folderArray.add(new Item(path, "Тут пусто, увы :("));
            return folderArray;                                                                     //возвращаем список папок (естественно, пустой) //надо это обработать, вылетает NPE
        }

        return joinLists(folderArray, itemArray);//itemList;
    }

    private List<Item> joinLists(List<Item> list1, List<Item> list2) {                              //метод соединяет списки папок и файлов и возвращает один список. Это нужно для
                                                                                                    //-- того, чтобы сначала шли папки, а потом файлы
        for (Item item : list2) {
            list1.add(item);
        }
        return list1;
    }

            /** !
    // массив может быть нулл, надо это обработать */
    private void sort(File[] pathsArray) {                                                          //метод для сортировки массива элементов в алфавитном порядке
        Comparator<File> comparator = (o1, o2) -> o1.getPath().compareToIgnoreCase(o2.getPath());
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

}


    /** СТАРЫЙ КОД (ЗАКОММЕНТИРОВАННЫЙ) */


//package com.andrey.guryanov.harmonica;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ItemListFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class ItemListFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
////////////////////////////////////////////////////////
//    private RecyclerView fileList;
//    private View view;
//    private Context parent;
//    private static FileListAdapter currentAdapter;
//    private ImageButton goBack, closeFileList;
////    private static List<String> navigation;
//    //private List<Item> files;
//
//    public ItemListFragment() {
//        // Required empty public constructor
//    }
//
//    public ItemListFragment(Context context) {
//        this.parent = context;
//    }
//
//
//    public static FileListAdapter getCurrentAdapter() {
//        return currentAdapter;
//    }
//
//    public static void setCurrentAdapter(FileListAdapter adapter) {
//        currentAdapter = adapter;
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ItemListFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ItemListFragment newInstance(String param1, String param2) {
//        ItemListFragment fragment = new ItemListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public void
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initViews();
//            //initRecyclerView();
//    }
//
//
//    protected void initViews() {
//        goBack = view.findViewById(R.id.ibtn_go_back);
//        closeFileList = view.findViewById(R.id.ibtn_go_to_title_screen);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_item_list, container, false);
//        //RecyclerView fileList = (RecyclerView) view.findViewById(R.id.rv_file_list);
//        initRecyclerView();
//        return view;
//    }
//
//
//    public void initRecyclerView() {
//        fileList = view.findViewById(R.id.rv_file_list);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parent);
//        fileList.setLayoutManager(layoutManager);
//        FileListAdapter adapter = new FileListAdapter(parent, view, fileList, FileList.getFirstPaths());
//        fileList.setAdapter(adapter);
//
//        currentAdapter = adapter;
//    }
//
//
//
//}
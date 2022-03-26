package com.y54.bdao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TestFragment extends Fragment {

    private View rootView;
    private ArrayList<HashMap> threadList = new ArrayList<>();
    private JSONArray arrays;
    private Context mContext;
    private SwipeRefreshLayout srl;
    private boolean isRefreshing;
    static String f;
    private RecyclerView mRv;
    final RecycleAdapter adapter = new RecycleAdapter(mContext,threadList);

    private int p,page=1;
    private HashMap<String,String> hashMap = new HashMap<>();
    private int id[]= new int[]{89, 45, 110, 111, 112, 113, 114, 115, 116, 90, 117, 118, 119, 93, 51, 96, 97, 98, 10, 11, 55, 99, 56, 12, 13, 14, -1, 15, 16, 17, 18, 19, 120, 121, 2, 3, 124, 4, 125, 126, 5, 6, 9, 20, 22, 23, 24, 25, 28, 29, 70, 72, 73, 30, 75, 31, 32, 33, 34, 35, 37, 38, 39, 100, 101, 103, 106, 107, 81, 108, 40, 86, 87};
    private String[] name = new String[]{"日记", "卡牌桌游", "社畜", "跑团", "城墙", "育儿", "询问3", "摄影2", "主播", "美漫", "技术支持", "宠物", "彩虹六号", "舰娘", "WOT", "圈内", "女装", "姐妹1", "Minecraft", "推理", "声优", "国漫", "考试", "漫画", "COSPLAY", "动画", "时间线", "科学", "偶像", "创意", "值班室", "小说", "围炉", "速报2", "游戏", "手游", "SE", "综合版1", "旅行", "占星", "东方Project", "VOCALOID", "特摄", "欢乐恶搞", "LOL", "暴雪游戏", "索尼", "任天堂", "怪物猎人", "AC大逃杀", "DOTA", "DNF", "EVE", "技术宅", "数码", "影视", "料理", "体育", "MUG", "音乐", "军武", "口袋妖怪", "模型", "艺人", "虚拟偶像", "文学", "买买买", "Steam", "都市怪谈", "微软", "猫版", "战争雷霆", "轻小说"};
    final String[] tabs = new String[]{"综合版1", "欢乐恶搞", "姐妹1","日记","女装","跑团","围炉"};


    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static TestFragment newInstance(int po, Context context) {

        Bundle args = new Bundle();
        args.putSerializable("array",po);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        fragment.setmContext(context);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_test, container, false);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        p= (getArguments() != null ? getArguments().getInt("array") : null);
        f = tabs[p];
    }
    protected void initView(){
        for (int i = 0; i <name.length ; i++) {
            hashMap.put(name[i], String.valueOf(id[i]));
        }
        mRv = rootView.findViewById(R.id.Rv);
        srl = rootView.findViewById(R.id.reFresh);
        srl.setColorSchemeColors(getResources().getColor(R.color.shojopink));
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                srl.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                srl.setRefreshing(true);
                isRefreshing = true;
                getDataWithOkHttp(1);
            }
        });
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                getDataWithOkHttp(1);
            }
        });
        mRv.setVisibility(View.INVISIBLE);
    }

    protected void getDataWithOkHttp(int page) {
        String url = "https://adnmb3.com/api/showf?";
        String aa = "20",bb=tabs[p];
        aa = hashMap.get(bb);
        Log.d("sssss", aa+bb);
        String URL1 = url + "id=" + aa +"&page="+page;
        OkHttpClientManager.getInstance().getAsync(URL1, new Callback() {
           @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String responseStr = response.body().string();
                notifyDataMap(responseStr);
            }
            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    private void notifyDataMap(String response) {
        Observable.just(response)
                .observeOn(Schedulers.io())
                .map(new Func1<String, Void>() {
                    @Override
                    public Void call(String s) {
                        decodeResult(s);
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        notifyDataRefresh();
                    }
                });

    }
    protected void notifyDataRefresh() {

        if(page==1){
            isRefreshing = false;
            srl.setRefreshing(false);
            mRv.setVisibility(View.VISIBLE);
            final LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter.setThreadList(threadList);
            mRv.setLayoutManager(manager);
            if (!adapter.hasObservers()) {
                adapter.setHasStableIds(true);
            }
            mRv.setAdapter(adapter);
            mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState==RecyclerView.SCROLL_STATE_IDLE){
                        LinearLayoutManager lm =manager;
                        int lastp = lm.findLastVisibleItemPosition();
                        if(lastp>=lm.getItemCount()-1){
                            page++;
                            getDataWithOkHttp(page);
                        }
                    }
                }
            });
        }
        else {
            adapter.setThreadList(threadList);
            adapter.notifyDataSetChanged();
        }
    }
    protected void decodeResult(String result) {
        try {
            if(page == 1)threadList.clear();
            arrays = new JSONArray(result);
            for (int i = 0; i <arrays.length() ; i++) {
                JSONObject jsonObject = (JSONObject)arrays.get(i);
                HashMap<String,String> threa = new HashMap<>();
                threa.put("code",jsonObject.optString("id"));
                threa.put("sendTime",jsonObject.optString("now"));
                threa.put("po",jsonObject.optString("userid"));
                threa.put("replyCount",jsonObject.optString("replyCount"));
                threa.put("content",jsonObject.optString("content"));
                threa.put("imgUrl",jsonObject.optString("img"));
                threa.put("ext",jsonObject.optString("ext"));
                threadList.add(threa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.VViewHolder>{

        private Context mContext;
        private ArrayList<HashMap> threadList;
        private HashMap<String,String> thread =new HashMap<>();


        public void setThreadList(ArrayList<HashMap> threadList) {
            this.threadList = threadList;
        }

        public RecycleAdapter(Context context, ArrayList<HashMap> threadList) {
            this.mContext = context;
            this.threadList = threadList;
        }



        @NonNull
        @Override
        public RecycleAdapter.VViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == 0)
            return new myViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.thread,parent,false));
            else return new FoVHr(LayoutInflater.from(getActivity()).inflate(R.layout.footview,parent,false));
        }
        @Override
        public long getItemId(int position) { return position;}

        @Override
        public void onBindViewHolder(@NonNull RecycleAdapter.VViewHolder holder, int position) {
            if(holder instanceof myViewHolder){
                thread = threadList.get(position);
                ((myViewHolder)holder).po.setText(thread.get("po"));
                ((myViewHolder)holder).sdt.setText(DateUtils.FormatDate(thread.get("sendTime")));
                ((myViewHolder)holder).rpc.setText(thread.get("replyCount"));
                final String u ="https://nmbimg.fastmirror.org/thumb/";
                ((myViewHolder)holder).img_out.setVisibility(View.GONE);
                if(!thread.get("imgUrl").isEmpty()){
                    ((myViewHolder)holder).img_out.setVisibility(View.VISIBLE);
                    final String s = "https://nmbimg.fastmirror.org/image/"+thread.get("imgUrl")+thread.get("ext");
                    Glide.with(getActivity())
                            .load(u+thread.get("imgUrl")+thread.get("ext"))
                            .placeholder(R.drawable.loadimg)
                            .skipMemoryCache(true).fitCenter()
                            .into(((myViewHolder)holder).img_out);
                    ((myViewHolder)holder).img_out.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),ImageActivity.class);
                            intent.putExtra("imgUrl",s);
                            startActivity(intent);
                        }
                    });
                }
                ((myViewHolder)holder).ct.setText(Html.fromHtml(thread.get("content")));
            }
        }
        @Override
        public int getItemCount() {
            return threadList.size()+1;
        }
        @Override
        public int getItemViewType(int position) {
            if(position>=threadList.size())return 1;
            else return 0;
        }
        class VViewHolder extends RecyclerView.ViewHolder {
            public VViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
        class FoVHr extends VViewHolder {
            public FoVHr(@NonNull View itemView) {
                super(itemView);
            }
        }
        class myViewHolder extends VViewHolder implements View.OnClickListener{
            private TextView po,sdt,rpc,ct;
            private ImageView img_out;
            private Context context;
            public myViewHolder(@NonNull View itemView) {
                super(itemView);
                this.po = itemView.findViewById(R.id.po);
                this.sdt = itemView.findViewById(R.id.send_time);
                this.rpc = itemView.findViewById(R.id.reply_count);
                this.ct = itemView.findViewById(R.id.content);
                this.img_out = itemView.findViewById(R.id.img);
                this.context =mContext;
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),ThreadActivity.class);
                i.putExtra("code",threadList.get((int) getItemId()).get("code").toString());
                i.putExtra("f",f);
                startActivity(i);
            }
        }
    }
}
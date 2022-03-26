package com.y54.bdao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

public class ThreadActivity extends AppCompatActivity {
    private ArrayList<HashMap> threadList = new ArrayList<>();
    private SwipeRefreshLayout srl;
    private int page=1;
    private boolean flag = false;
    private RecyclerView mRv;
    private String id,TAG="aaaaaaa",fname;
    private final RecycleAdapter adapter = new RecycleAdapter(this,threadList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.setStatusBarColor(Color.parseColor("#fa7298"));//设置状态栏颜色
        Intent i = getIntent();
        id = i.getStringExtra("code");
        fname = i.getStringExtra("f");
        mRv = findViewById(R.id.RvIn);
        srl = findViewById(R.id.reFreshIn);
        srl.setColorSchemeColors(getResources().getColor(R.color.shojopink));
        srl.setRefreshing(true);
        getDataWithOkHttp(1);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataWithOkHttp(1);
                page =1;
            }
        });
        mRv.setVisibility(View.INVISIBLE);
    }

    protected void getDataWithOkHttp(int page) {
        String url = "https://adnmb3.com/Api/thread?";
        String URL1 = url + "id=" + id + "&page=" + page;
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
            srl.setRefreshing(false);
            mRv.setVisibility(View.VISIBLE);
            flag = true;
            final LinearLayoutManager manager = new LinearLayoutManager(this);
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
            JSONObject thread = new JSONObject(result);
            if(page==1){
                threadList.clear();
                HashMap<String, String> thre = new HashMap<>();
                thre.put("code", thread.optString("id"));
                thre.put("sendTime", thread.optString("now"));
                thre.put("imgUrl", thread.optString("img"));
                thre.put("ext", thread.optString("ext"));
                thre.put("po", thread.optString("userid"));
                thre.put("admin", thread.optString("admin"));
                thre.put("content", thread.optString("content"));
                threadList.add(thre);
            }
            JSONArray arrays = thread.optJSONArray("replys");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject jsonObject = (JSONObject) arrays.get(i);
                HashMap<String, String> threa = new HashMap<>();
                threa.put("code", jsonObject.optString("id"));
                if(threa.get("code").compareTo("9999999")==0){
                    threa.clear();
                    continue;
                }
                threa.put("sendTime", jsonObject.optString("now"));
                threa.put("imgUrl", jsonObject.optString("img"));
                threa.put("ext", jsonObject.optString("ext"));
                threa.put("po", jsonObject.optString("userid"));
                threa.put("content", jsonObject.optString("content"));
                threa.put("admin", jsonObject.optString("admin"));
                threadList.add(threa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{

        private Context mContext;
        private ArrayList<HashMap> threadList;
        private HashMap<String,String> thread =new HashMap<>();
        private int l=0;

        public RecycleAdapter(Context context,ArrayList<HashMap> threadList) {
            this.mContext = context;
            this.threadList = threadList;
        }

        public void setThreadList(ArrayList<HashMap> threadList) {
            this.threadList = threadList;
        }

        @NonNull
        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == 0)
            return new RecycleAdapter.myViewHolder(LayoutInflater.from(mContext).inflate(R.layout.thread_in,parent,false));
            else if(viewType==1)  return new RecycleAdapter.FootViewHolder(LayoutInflater.from(mContext).inflate(R.layout.footview,parent,false));
            else return new RecycleAdapter.FootViewHolder(LayoutInflater.from(mContext).inflate(R.layout.footview,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if(holder instanceof myViewHolder){
                thread = threadList.get(position);
                final String po=((HashMap<String,String>)threadList.get(0)).get("po");
                ((myViewHolder)holder).po.setText(thread.get("po"));
                if(position==0){
                    ((myViewHolder)holder).fname.setVisibility(View.VISIBLE);
                    ((myViewHolder)holder).fname.setText(fname);
                }else{
                    ((myViewHolder)holder).fname.setVisibility(View.GONE);
                }

                if(thread.get("po").compareTo("ATM")==0){
                    ((myViewHolder) holder).up.setImageResource(R.drawable.dahuiyuan);
                    ((myViewHolder) holder).up.setVisibility(View.VISIBLE);
                    ((myViewHolder) holder).lv.setImageResource(R.drawable.lv6);
                    ((myViewHolder) holder).po.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }

                else if(thread.get("po").compareTo(po)!=0){
                    ((myViewHolder) holder).up.setImageResource(R.drawable.up);
                    ((myViewHolder) holder).up.setVisibility(View.GONE);
                    ((myViewHolder) holder).po.setTextColor(Color.parseColor("#888888"));
                    ((myViewHolder) holder).po.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    char a = thread.get("po").charAt(0);
                    int i = Integer.valueOf(a)%6;
                    switch (i){
                        case 0:
                            ((myViewHolder) holder).lv.setImageResource(R.drawable.lv0);
                            break;
                        case 1:
                            ((myViewHolder) holder).lv.setImageResource(R.drawable.lv1);
                            break;
                        case 2:
                            ((myViewHolder) holder).lv.setImageResource(R.drawable.lv2);
                            break;
                        case 3:
                            ((myViewHolder) holder).lv.setImageResource(R.drawable.lv3);
                            break;
                        case 4:
                            ((myViewHolder) holder).lv.setImageResource(R.drawable.lv4);
                            break;
                        case 5:
                            ((myViewHolder) holder).lv.setImageResource(R.drawable.lv5);
                            break;
                        default:break;
                    }

                }else{
                    ((myViewHolder) holder).lv.setImageResource(R.drawable.lv6);
                    ((myViewHolder) holder).up.setVisibility(View.VISIBLE);
                    ((myViewHolder) holder).po.setTextColor(Color.parseColor("#000000"));
                    ((myViewHolder) holder).po.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
                if(thread.get("admin").compareTo("0")!=0){
                    ((myViewHolder) holder).po.setTextColor(Color.parseColor("#ff0000"));
                }
                ((myViewHolder)holder).sdt.setText(DateUtils.FormatDate(thread.get("sendTime")));
                ((myViewHolder)holder).code.setText("No."+thread.get("code"));
                String u ="https://nmbimg.fastmirror.org/image/";
                ((myViewHolder) holder).img_in.setVisibility(View.GONE);
                if(!thread.get("imgUrl").isEmpty()){
                    final String s = "https://nmbimg.fastmirror.org/image/"+thread.get("imgUrl")+thread.get("ext");
                    ((myViewHolder) holder).img_in.setVisibility(View.VISIBLE);
                    Glide.with(ThreadActivity.this)
                            .load(u+thread.get("imgUrl")+thread.get("ext"))
                            .placeholder(R.drawable.loadimg)
                            .skipMemoryCache(true).fitCenter()
                            .dontTransform()
                            .dontAnimate().diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(((myViewHolder)holder).img_in);
                    ((myViewHolder) holder).img_in.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ThreadActivity.this,ImageActivity.class);
                            intent.putExtra("imgUrl",s);
                            startActivity(intent);
                        }
                    });
                }
                ((myViewHolder)holder).ct.setText(Html.fromHtml(thread.get("content")));
                String text = thread.get("content");

                final SpannableStringBuilder style = new SpannableStringBuilder();
                style.append(thread.get("content"));
            }
            else {

            }

        }

        @Override
        public long getItemId(int position)

        { return position;}


        @Override
        public int getItemViewType(int position) {


            if(position>=threadList.size()){
                if(l<threadList.size()){
                    l = threadList.size();
                    return 1;
                }else {
                    l = threadList.size();
                    return 2;
                }

            }
            else {
                return 0;
            }
        }

        @Override
        public int getItemCount() {
            return threadList.size()+1;
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
        class FootViewHolder extends ViewHolder {
            private ImageView furry;
            private TextView tip;
            public FootViewHolder(@NonNull View itemView) {
                super(itemView);
                this.furry = itemView.findViewById(R.id.furry);
                this.tip = itemView.findViewById(R.id.tips);
            }
        }

        class myViewHolder extends ViewHolder implements View.OnClickListener{
            private TextView po,sdt,code,ct,fname;
            private ImageView img_in,lv,up;
            private Context context;
            public myViewHolder(@NonNull View itemView) {
                super(itemView);
                this.po = (TextView)itemView.findViewById(R.id.poin);
                this.sdt = (TextView)itemView.findViewById(R.id.send_time_in);
                this.code = (TextView)itemView.findViewById(R.id.inCode);
                this.ct = (TextView)itemView.findViewById(R.id.content_in);
                this.img_in = itemView.findViewById(R.id.img_in);
                this.lv = itemView.findViewById(R.id.lv);
                this.up = itemView.findViewById(R.id.up);
                this.fname = itemView.findViewById(R.id.fname);
                this.context =mContext;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                
            }
        }
    }
}

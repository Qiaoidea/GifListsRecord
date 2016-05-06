package me.qiao.gifcard.api;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.qiao.gifcard.bean.GifBean;

/**
 * Created by Qiao on 2016/5/4.
 * function：
 */
public class GifDataApi {
    private static final String GIF_SERVER = "http://www.xiaohuazu.com";
    public static final String  IMAGE_DISK_CACHE;
    static {
        IMAGE_DISK_CACHE = String.format("%s%s",
                Environment.getExternalStorageDirectory().getAbsolutePath(),"/QImageCache/");
        File file = new File(IMAGE_DISK_CACHE);
        if(!file.exists()){
            file.mkdir();
        }
    }

    /**
     * 设置图片缓存目录
     * @param context
     */
    public static void setupGlide(Context context){
        if (!Glide.isSetup()) {
            GlideBuilder gb = new GlideBuilder(context);
            DiskCache dlw = DiskLruCacheWrapper.get(new File(GifDataApi.IMAGE_DISK_CACHE), 150 * 1024 * 1024);
            gb.setDiskCache(dlw);
            Glide.setup(gb);
        }
    }

    /**
     * 使用Jsoup获取网页body,解析数据拿到gif 列表
     * @param pageNo
     * @return
     */
    public static List<GifBean> getGifOnline(int pageNo){
        List<GifBean> data = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(buildUrl(pageNo)).get();
            Element content = doc.body().getElementsByClass("list").first();
            Elements gifs = content.select("img[src$=.gif]");

            for(Element element:gifs){
                GifBean bean = new GifBean();
                bean.setUrl(realPath(element.attr("src")));
                bean.setDesc(element.attr("alt"));
                data.add(bean);
//                System.out.println(element.toString());//"src").toString()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 从本地获取 gif 列表（ Glide 缓存 gif格式后缀为.0）
     * @return
     */
    public static File[] getGifOffline(){
//        mFiles = Glide.getPhotoCacheDir(getContext())
//                .listFiles();

        return new File(GifDataApi.IMAGE_DISK_CACHE).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".0");
            }
        });
    }

    /**
     * 拼接每页详细地址
     * http://www.xiaohuazu.com/gif/list_xx.html  (xx： pageNo)
     * @param pageNo
     * @return
     */
    private static String buildUrl(int pageNo){
        return String.format("%s/gif/list_%d.html",GIF_SERVER,pageNo);
    }

    /**
     * 为 相对地址 添加 target头
     * "uploads/allimg/121214/1-12121412533R58.gif"
     *  -->  "http://www.xiaohuazu.com/uploads/allimg/121214/1-12121412533R58.gif"
     * @param url
     * @return
     */
    private static String realPath(String url){
        if(url.startsWith("http")){
            return url;
        }
        return String.format("%s%s",GIF_SERVER,url);
    }
}

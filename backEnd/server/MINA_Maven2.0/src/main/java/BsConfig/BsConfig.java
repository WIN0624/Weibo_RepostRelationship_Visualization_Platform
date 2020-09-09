package BsConfig;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import BsConfig.FileHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BsConfig {
    protected final static Logger logger = Logger.getLogger(BsConfig.class);

    public final static String CF_THREADNUM = "threadNumber";
    public final static String CF_PORT = "port";
    public final static String CF_DATADIR = "dataDir";
    public final static String CF_LINECHARLIMIT = "lineCharLimit";
    public final static String CF_DEFAULTEVENTFILENAME = "defaultEventFileName";
    public final static String CF_BASEFILENAME = "baseFileName";
    public final static String CF_BASEINFOFILENAME = "baseInfoFileName";
    public final static String CF_EVENTFILENAMEPREFIX = "eventFileNamePrefix";
    public final static String CF_EVENTMAXLINE = "eventMaxLine";
    public final static String CF_UPDATESLEEPMILLIS = "updateExecutorSleepMillis";
    public final static String CF_BASEINFODUMPPATH = "baseInfoDumpPath";
    public final static String CF_BASEFULLDUMPPATH = "baseFullDumpPath";

    /*
     * 连接Qserver配置
     */
    public final static String Q_SERVER_PORT = "9600";
    public final static String Q_SERVER_HOST = "10.48.56.21";

    /*
     * 可指定当前推广时段
     */
    public final static String ABEYANCE_CYCLE_NUMBER = "abeyanceCycleNumber_debug";

    /*
     * 可指定创意选取是否从头开始
     */
    public final static String SELECT_IDEA_FROM_BEGIN = "selectIdeaFromBegin_debug";

    // FulltextDb配置
    public final static String CF_FULLTEXTDB_USENLP = "usenlp";
    public final static String CF_FULLTEXTDB_DICTPATH = "dictPath";

    //search相关配置
    public final static String SEARCH_MAX_WINFOID_NUMBER = "searchMaxWinfoidNumber";
    public final static String SEARCH_MAX_WORDID_NUMBER = "searchMaxWordidNumber";
    public final static String SELECT_IDEA_RANDOM_RETRYTIMES = "selectIdeaRandomRetrytimes";

    /*
     * 获取配对之前，同一单元下最多取多少个创意
     */
    public final static String IDEAINFO_MAX_RESULT_IN_UNIT = "ideanumMaxInUnit";

    /*
     * 请求Q server一次的pair数量上限
     */
    public final static String QSERVER_MAX_PAIR = "qserverMaxPair";

    public final static String CF_PREWORDSEGDATA = "preWordsegData";

    /*
     * 预估user层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_USER_LEVEL_NUM = "userLevelNum";

    /*
     * 预估plan层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_PLAN_LEVEL_NUM = "planLevelNum";

    /*
     * 预估unit层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_UNIT_LEVEL_NUM = "unitLevelNum";

    /*
     * 预估winfo层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_WINFO_LEVEL_NUM = "winfoLevelNum";

    /*
     * 预估idea层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_IDEA_LEVEL_NUM = "ideaLevelNum";

    /*
     * 预估word数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_WORD_LEVEL_NUM = "wordLevelNum";

    /*
     * 预估hospital层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_HOSPITAL_LEVEL_NUM = "hospitalLevelNum";

    /*
     * 预估pair层级数据量（用于正排/倒排索引初始大小）
     */
    public final static String CF_PAIR_LEVEL_NUM = "pairLevelNum";

    /*
     * 是否启用bid打折
     */
    public final static String CF_USE_BID_DISCOUNT = "useBidDiscount";

    /*
     * 是否启用找疾病卡片逻辑
     */
    public final static String CF_USE_SEARCH_DISEASE = "useSearchDisease";

    protected static Gson gson = new Gson();
    static HashMap<String, String> data;
    static String conf;
    static long lastModified = 0;

    public static String get(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        }
        return null;
    }

    public static int getInt(String key) {
        String val = get(key);
        if (val != null) {
            return Integer.parseInt(val);
        }
        return 0;
    }

    public static boolean getBool(String key) {
        String val = get(key);
        if (val != null) {
            return Boolean.parseBoolean(val);
        }
        return false;
    }

    public static long getLong(String key) {
        String val = get(key);
        if (val != null) {
            return Long.parseLong(val);
        }
        return 0;
    }

    public static float getFloat(String key) {
        String val = get(key);
        if (val != null) {
            return Float.parseFloat(val);
        }
        return 0.0f;
    }

    public static void load(String conf, boolean check) throws Exception {
        BsConfig.conf = conf;
        reload();
        if (check) {
            new ReloadThread().start();
        }
    }

    public static void load(String conf) throws Exception {
        load(conf,true);
    }

    public static void reload() throws Exception {
        URL url = FileHelper.class.getClassLoader().getResource(conf);
        File file = new File(url.getFile());
        if (!file.exists()) {
            logger.warn("file is not exists : " + conf);
            return;
        }
        if (file.lastModified() <= lastModified) {
            // logger.debug("file is not modified : " + conf);
            return;
        }
        String json = FileHelper.readFileContent(conf);
        logger.info("reload config begin :" + conf);
        logger.info("config:" + json);
        data = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
        }.getType());
        logger.info("reload config end:" + conf);
        lastModified = file.lastModified();

    }

    static class ReloadThread extends Thread {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    reload();
                } catch (Exception e) {

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BsConfig.load("/conf/bs.conf");
    }

}

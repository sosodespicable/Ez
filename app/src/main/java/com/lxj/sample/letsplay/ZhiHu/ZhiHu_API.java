package com.lxj.sample.letsplay.ZhiHu;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class ZhiHu_API {
    //知乎日报api的http method均为get
    //将文章id拼接在base url的后面就可以
    public static final String ZHIHU_DAILY_BASE_URL = "http://news-at.zhihu.com/story/";
    //获取界面启动图像
    public static final String START_IMAGE = "http://news-at.zhihu.com/api/4/start-image/1080*1776";
    //最新消息
    public static final String LATEST = "http://news-at.zhihu.com/api/4/news/latest";
    //消息内容获取与离线下载
    //在最新消息中获取到的id，拼接到这个NEWS之后，可以获得对应的JSON格式的内容
    public static final String NEWS = "http://news-at.zhihu.com/api/4/news/";
    //过往消息
    //比如11.18的消息为20161118
    //知乎日报的生日为2013 年 5 月 19 日，如果before后面的数字小于20130520，那么只能获取到空消息
    public static final String HISTORY = "http://news.at.zhihu.com/api/4/news/before/";
    //新闻额外消息，如评论数量，赞...
    public static final String STORY_EXTRA = "http://news-at.zhihu.com/api/4/story-extra/";
    // 新闻对应长评论查看
    // long comments of post
    // 使用在 最新消息 中获得的 id
    // use the post id that you got in latest posts
    // 在 http://news-at.zhihu.com/api/4/story/#{id}/long-comments 中将 id 替换为对应的 id
    // replace id
    // 得到长评论 JSON 格式的内容
    // get the long comment as json format
    // 新闻对应短评论查看
    // short comment of post
    // http://news-at.zhihu.com/api/4/story/4232852/short-comments
    // 使用在 最新消息 中获得的 id
    // use the post id that you got in latest posts
    // 在 http://news-at.zhihu.com/api/4/story/#{id}/short-comments 中将 id 替换为对应的 id
    // replace id
    // 得到短评论 JSON 格式的内容
    // get the short comment as json format
    public static final String COMMENTS = "http://news-at.zhihu.com/api/4/story/";
    // 主题日报列表查看
    // Theme posts
    public static final String THEMES = "http://news-at.zhihu.com/api/4/themes";

    // 主题日报内容查看
    // check out the content of theme post
    // http://news-at.zhihu.com/api/4/theme/11
    // 使用在 主题日报列表查看 中获得需要查看的主题日报的 id
    // similarly, use the id you got in theme post list
    // 拼接在 http://news-at.zhihu.com/api/4/theme/ 后
    // add it to http://news-at.zhihu.com/api/4/theme/
    // 得到对应主题日报 JSON 格式的内容
    // just like the latest post, add the id you got in theme post, and u will get the content as json format
    public static final String THEME = "http://news-at.zhihu.com/api/4/theme/";
    // 热门消息
    // hot posts
    // 请注意！ 此 API 仍可访问，但是其内容未出现在最新的『知乎日报』 App 中。
    // Please pay attention to this api. It is accessible yet, but it doesn't appear in the ZhihuDaily APP.
    public static final String HOT = "http://news-at.zhihu.com/api/3/news/hot";

    // 查看新闻的推荐者
    // checkout the recommenders
    // "http://news-at.zhihu.com/api/4/story/#{id}/recommenders"
    // 将新闻id填入到#{id}的位置
    // replace the #{id} with the id you got.

    // 获取某个专栏之前的新闻
    // acquire the past posts of one column
    // http://news-at.zhihu.com/api/4/theme/#{theme id}/before/#{id}
    // 将专栏id填入到 #{theme id}, 将新闻id填入到#{id}
    // put column id into #{theme id}, put post id into #{id}
    // 如 http://news-at.zhihu.com/api/4/theme/11/before/7119483
    // just like http://news-at.zhihu.com/api/4/theme/11/before/7119483
    // 注：新闻id要是属于该专栏，否则，返回结果为空
    // attention: the post id must belong to that column, or you will got a null value

    // 查看editor的主页
    // check out the home page of editor
    // http://news-at.zhihu.com/api/4/editor/#{id}/profile-page/android

    // Guokr base url
    public static final String GUOKR_ARTICLE_BASE_URL = "http://apis.guokr.com/handpick/article.json";

    // 获取果壳精选的文章列表,通过组合相应的参数成为完整的url
    // Guokr handpick articles. make complete url by combining params
    public static final String GUOKR_ARTICLES = "http://apis.guokr.com/handpick/article.json?retrieve_type=by_since&category=all&limit=20&ad=1";

    // 获取果壳文章的具体信息 V1
    // specific information of guokr post V1
    public static final String GUOKR_ARTICLE_LINK_V1 = "http://jingxuan.guokr.com/pick/";

    // 获取果壳文章的具体信息 V2
    // V2
    public static final String GUOKR_ARTICLE_LINK_V2 = "http://jingxuan.guokr.com/pick/v2/";

    // 获取果壳精选的轮播文章列表
    // carousel posts
    // public static final String GUOKR_HANDPICK_CAROUSEL = "http://apis.guokr.com/flowingboard/item/handpick_carousel.json";

}

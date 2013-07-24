package com.easou.news.crawl.service.impl;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easou.news.crawl.constant.CommandContant;
import com.easou.news.crawl.factory.TemplateFactory;
import com.easou.news.crawl.model.CrawlBasicInfo;
import com.easou.news.crawl.model.CrawlImage;
import com.easou.news.crawl.model.CrawlUrl;
import com.easou.news.crawl.model.Template;
import com.easou.news.crawl.model.TemplateBasicInfo;
import com.easou.news.crawl.model.TemplateContentPage;
import com.easou.news.crawl.model.TemplateTagWeight;
import com.easou.news.crawl.service.IExtractService;
import com.easou.news.crawl.service.IHttpClientService;
import com.easou.news.crawl.service.IRedisService;
import com.easou.news.crawl.service.ITransformService;
import com.easou.news.crawl.util.ContentFilterUtil;
import com.easou.news.crawl.util.DateUtil;
import com.easou.news.crawl.util.HtmlUtil;
import com.easou.news.crawl.util.MatcherUtil;
import com.easou.news.crawl.util.Md5Util;
import com.easou.news.crawl.util.UrlUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class ExtractServiceImpl implements IExtractService {
	private static Logger logger = LoggerFactory.getLogger(ExtractServiceImpl.class);
	
	@Resource(name="templateFactory")
	private TemplateFactory templateFactory;
	@Autowired
	private IHttpClientService httpClientService;
	@Autowired
	private ITransformService transformService;
    @Autowired(required = false)
    private IRedisService redisService;
	
	@Override
	public List<CrawlUrl> extractHtmlCatalog(CrawlUrl catalogUrl) {
		if (StringUtil.isBlank(catalogUrl.getBody())) {
			return new ArrayList<CrawlUrl>();
		}
		StopWatch stopWatch = new Log4JStopWatch();
		List<CrawlUrl> crawlUrls = new ArrayList<CrawlUrl>();
		Template template = templateFactory.getTemplate(catalogUrl.getEntryWay());
		
		Document document = Jsoup.parse(catalogUrl.getBody(),catalogUrl.getBaseUri());
		Elements elements = null;
		
		if(template.getRankInfo() != null){
			elements = document.select(template.getRankInfo());
			for(int i = 0 ; i < elements.size() ; i++){
			    CrawlUrl crawlUrl = packageUrlAndBasicInfo(catalogUrl, elements.get(i), template, i+1);
			    crawlUrls.add(crawlUrl);
				logger.info("rankOrder : " + crawlUrl.getRankOrder() + " url : " + crawlUrl.getUri().toString());
			}
		} else {
			if (StringUtils.isNotBlank(template.getContentExpression())) {
				Elements contentEl = contentExpressionArea(template.getContentExpression(), document);
				document = Jsoup.parse(contentEl.outerHtml(),catalogUrl.getBaseUri());
			}
			elements = document.getElementsByTag("a");
			for (Element element : elements) {
				String url = "";
				try {
					url = element.attr("abs:href");
					if(StringUtils.isBlank(url) || catalogUrl.getEntryWay().equals(url) || catalogUrl.getUri().toString().equals(url)) {
						continue;
					}
					
					url = url.replaceAll("#[^#?&]+", "");
//				System.out.println(url + "\t" + template.getContentRegexs());
					if(MatcherUtil.isMatch(url, template.getFilterRegexs())) {
						
						continue;
					}
					url = url.trim();
					if(MatcherUtil.isMatch(url, template.getContentRegexs())){
						CrawlUrl crawlUrl = new CrawlUrl(catalogUrl.getEntryWay(), URI.create(url), 1, 0);
						crawlUrl.setCatalog_title(element.text());
						int weight = getNewsWeight(element, template.getTemplateTagWeights());
						crawlUrl.setWeight(weight);
						if( weight > 1) {
							// Ugly: 修改原始 url, 加后缀
							crawlUrl.setUri(URI.create(url + CommandContant.KEYNEWSTAG));
							logger.info("key news' url modified, adding a suffix: " + CommandContant.KEYNEWSTAG + url);
						} else if (template.getClassify().equals("jiaodian")) {
							crawlUrl.setWeight(2);
							crawlUrl.setUri(URI.create(url + CommandContant.KEYNEWSTAG));
							logger.info("key news' url modified, adding a suffix: " + CommandContant.KEYNEWSTAG + url);
						}
						crawlUrls.add(crawlUrl);
					}
					if(catalogUrl.getCatalog_level() == 0) {
						if(MatcherUtil.isMatch(url, template.getWebNameRegexs())){
							CrawlUrl crawlUrl = new CrawlUrl(catalogUrl.getEntryWay(), URI.create(url), 0, 1);
							crawlUrl.setForce(true);
							crawlUrls.add(crawlUrl);
						}
					}
				} catch (Exception e) {
					logger.error("catalog url:" + url, e);
				}
			}
		}
		
		stopWatch.stop("extract catalog");
		return crawlUrls;
	}
	
	/**
     * 封装basic信息，并返回crawlUrl对象
     * @param catalogUrl
     * @param element
     * @param template
     * @param rankOrder
     * @return
     */
    public CrawlUrl packageUrlAndBasicInfo(CrawlUrl catalogUrl, Element element, Template template, int rankOrder){
        
        CrawlUrl crawlUrl = new CrawlUrl(catalogUrl.getEntryWay(), URI.create(element.attr("abs:href")), 0, 1);
        crawlUrl.setRankInfo(template.getRankInfo());
        crawlUrl.setRankOrder(rankOrder);
        
        CrawlBasicInfo crawlBasicInfo = new CrawlBasicInfo();
        crawlBasicInfo.setEntryWay(crawlUrl.getEntryWay());
        crawlBasicInfo.setPageUrl(crawlUrl.getUri().toString());
        crawlBasicInfo.setRankOrder(crawlUrl.getRankOrder());
        redisService.pushRankInfo(CommandContant.NEW_RANKINFO_LIST, crawlBasicInfo);
        
        logger.info("crawlBasicInfo : " + crawlBasicInfo);
        return crawlUrl;
    }

	@Override
	public CrawlBasicInfo extractHtmlContent(CrawlUrl contentUrl) {
		if (StringUtil.isBlank(contentUrl.getBody())) {
			return new CrawlBasicInfo();
		}
		StopWatch stopWatch = new Log4JStopWatch();
		Template template = templateFactory.getTemplate(contentUrl.getEntryWay());
		
		if (ContentFilterUtil.filterContent(template.getSource(), contentUrl.getBody())) {
			return new CrawlBasicInfo();
		}
		
		Document document = Jsoup.parse(contentUrl.getBody(), contentUrl.getBaseUri());
		CrawlBasicInfo crawlBasicInfo = packageBasicInfo(contentUrl, template, document);//创建CrawlBasicInfo对象并封装一些基础信息
		
		List<TemplateBasicInfo> templateBasicInfos = template.getTemplateBasicInfos();
		for(TemplateBasicInfo templateBasicInfo : templateBasicInfos) {
			boolean result = parseAndPackage(templateBasicInfo, contentUrl.getBody(), document, crawlBasicInfo);//解析相应的内容并封装到CrawlBasicInfo中
			if (!result) {
				return new CrawlBasicInfo();
			}
		}
		
		TemplateBasicInfo contentTemplate = getContentTemplate(template.getTemplateBasicInfos());//获得内容页的模板
		if(contentTemplate != null) {
			boolean res = mergePageContent(contentUrl, template, contentTemplate, document, crawlBasicInfo, 0);//迭代做合页操作
			if (!res) {
				return new CrawlBasicInfo();
			}
		}
		
		stopWatch.stop("content catalog");
		return crawlBasicInfo;
	}
	
	public CrawlBasicInfo transformHtmlContent(CrawlUrl contentUrl) {
		logger.info("transformHtmlContent:" + contentUrl.toString());
		StopWatch stopWatch = new Log4JStopWatch();
		Template template = templateFactory.getTemplate(contentUrl.getEntryWay());
		CrawlBasicInfo crawlBasicInfo = packageBasicInfo(contentUrl, template, null);//创建CrawlBasicInfo对象并封装一些基础信息

        String transformedContent = transformService.transform(contentUrl.getBody().getBytes());
        Document document = Jsoup.parse(transformedContent);
        
        Element main = document.getElementById("Main");
        Elements tElements = document.getElementsByTag("title_tag");
        String pageTitle = "";
        String title = "";
        if (main != null) {
            pageTitle = main.attr("title");	
        }
        if (tElements != null) {
            title = tElements.text();        	
        }
        crawlBasicInfo.setPageTitle(pageTitle);
        crawlBasicInfo.setContentTitle(title);
        Elements ctElements = document.getElementsByTag("content_tag");
        if (ctElements != null && ctElements.size() != 0) {
	        Element contentELement = ctElements.get(0);
	        Elements elements =  contentELement.getElementsByTag("img");      
	        List<CrawlImage> imgList = transformImages(elements, contentUrl.getBaseUri());
	        if (!imgList.isEmpty()) {
	        	crawlBasicInfo.setHasImage(true);
	        	crawlBasicInfo.setCrawlImages(imgList);
	        }
	        crawlBasicInfo.setPublishTime(System.currentTimeMillis());
	        crawlBasicInfo.setContent(contentELement.html());
        }
        
		stopWatch.stop("transform content catalog");
		return crawlBasicInfo;
	}
	
	private List<CrawlImage> transformImages(Elements elements, String entryWay) {
        List<CrawlImage> imageList = new ArrayList<CrawlImage>();
        for (Element element : elements) {
        	String alt = element.attr("alt");
        	String url = element.attr("src");
        	url = url.substring(url.lastIndexOf("url=") + 4);
        	url = UrlUtil.joinUrl(entryWay, url);
        	Element parent = element.parent();
        	
			String urlMd5 = Md5Util.generateMd5ByUrl(url);
			if(urlMd5 == null){
				logger.error("generationUrlMd5Fail: url[{}]", url);
				parent.remove();
				continue;
			}
			
			String reffer = null;
			if (entryWay.indexOf("81un") > 0) {
				reffer = entryWay;
			}
			byte[] data = httpClientService.getImageByGet(url, reffer);
			if(data == null){
				logger.error("downloadImageEmpty: url[{}]", url);
				parent.remove();
				continue;
			}
			parent.replaceWith((new TextNode("(key:"+urlMd5+")","")));
	    	CrawlImage crawlImage = new CrawlImage();
	    	crawlImage.setUrl(url);
	    	crawlImage.setAlt(alt);
	    	crawlImage.setMd5(urlMd5);
	    	crawlImage.setData(data);
	    	imageList.add(crawlImage);        	
        }
        return imageList;
	}
	

	private boolean mergePageContent(CrawlUrl contentUrl, Template template, TemplateBasicInfo contentTemplate, Document document, CrawlBasicInfo crawlBasicInfo, int count) {
		count = count + 1;
		if (count > 30) {
			return false;
		}
		String nextPageUrl = parseNextPageUrl(template, contentUrl, document);//解析下一页的url
		if(nextPageUrl == null)
			return true;
		
		String htmlBody = httpClientService.getResponseBodyByGet(nextPageUrl, template.getCharset());
		if(htmlBody == null)
			return true;
		contentUrl.setBody(htmlBody);
		
		document = Jsoup.parse(contentUrl.getBody(), contentUrl.getBaseUri());
		CrawlBasicInfo tempCrawlBasicInfo = new CrawlBasicInfo();
		tempCrawlBasicInfo.setEntryWay(crawlBasicInfo.getEntryWay());
		tempCrawlBasicInfo.setHost(contentUrl.getBaseUri());
		boolean resulst = parseAndPackage(contentTemplate, htmlBody, document, tempCrawlBasicInfo);//解析相应的内容并封装到CrawlBasicInfo中
		if (!resulst) {
			return false;
		}
		if(tempCrawlBasicInfo.isHasImage()){
			crawlBasicInfo.setHasImage(true);
			crawlBasicInfo.getCrawlImages().addAll(tempCrawlBasicInfo.getCrawlImages());	
		}
		if(StringUtils.isNotBlank(tempCrawlBasicInfo.getContent()))
			crawlBasicInfo.setContent(crawlBasicInfo.getContent()+"\n"+tempCrawlBasicInfo.getContent());
		
		return mergePageContent(contentUrl, template, contentTemplate, document, crawlBasicInfo, count);
	}

	private CrawlBasicInfo packageBasicInfo(CrawlUrl contentUrl, Template template, Document document) {
		CrawlBasicInfo crawlBasicInfo = new CrawlBasicInfo();
		crawlBasicInfo.setEntryWay(contentUrl.getEntryWay());
		crawlBasicInfo.setPageUrl(contentUrl.getUri().toString());
		if (document != null) {
			crawlBasicInfo.setPageTitle(document.title());
		}
		crawlBasicInfo.setCatalogTitle(contentUrl.getCatalog_title());
		crawlBasicInfo.setWeight(contentUrl.getWeight());
		crawlBasicInfo.setSource(template.getSource());
		crawlBasicInfo.setClassify(template.getClassify());
		crawlBasicInfo.setDownloadTime(DateUtil.currentDateMilliseconds());
		crawlBasicInfo.setHost(contentUrl.getBaseUri());
		return crawlBasicInfo;
	}

	private TemplateBasicInfo getContentTemplate(List<TemplateBasicInfo> templateBasicInfos) {
		for(TemplateBasicInfo templateBasicInfo : templateBasicInfos)
			if("content".equals(templateBasicInfo.getProperty()))
				return templateBasicInfo;
		return null;
	}

	private String parseNextPageUrl(Template template, CrawlUrl contentUrl, Document document){
		TemplateContentPage contentPage = template.getTemplateContentPage();
		if(contentPage != null){
			String fixedContent = fixedHtmlContent(contentPage.getAction(), contentPage.getExpression(), contentUrl.getBody(), document);//根据表达式得到指定的内容
			if(StringUtils.isNotBlank(fixedContent)){
				Pattern pattern = Pattern.compile(contentPage.getUrlRegex());
				Matcher matcher = pattern.matcher(fixedContent);
				if(matcher.find()){
					String relUrl = matcher.group(1);
					return UrlUtil.joinUrl(contentUrl.getUri().toString(), relUrl);
				}
			}
		}
		
		return null;
	}

	private boolean parseAndPackage(TemplateBasicInfo templateBasicInfo, String htmlBody, Document document, CrawlBasicInfo crawlBasicInfo){
		String value = fixedHtmlContent(templateBasicInfo.getAction(), templateBasicInfo.getExpression(), htmlBody, document);//根据表达式得到指定的内容
		if(value != null){
			value = HtmlUtil.removeScriptTags(value);//去掉script、style等标签
			value = HtmlUtil.replaceSign(value);//替换&nbsp;等标记
			Document valueDoc = Jsoup.parse(value, crawlBasicInfo.getHost());
			Element valueBody = valueDoc.body();
			//下载图片
			if(templateBasicInfo.isHasImage()){
				Elements imageElements = valueBody.getElementsByTag("img");
				List<CrawlImage> crawlImages = downloadContentImage(imageElements, crawlBasicInfo.getHost());
				if (crawlImages == null) {
					return false;
				}
				crawlBasicInfo.setHasImage(crawlImages.size()>0);
				crawlBasicInfo.setCrawlImages(crawlImages);
			}

			//保留指定的标签
			if(StringUtils.isNotBlank(templateBasicInfo.getHoldTags()))
				value = HtmlUtil.keepFixedHtml(valueBody.html(), templateBasicInfo.getHoldTags());
			else
				value = valueBody.text();

			//去掉空白行
			value = HtmlUtil.removeSpaceLine(value);
			
			//格式化日期， 通过反射来设置属性
			if(StringUtils.isNotBlank(templateBasicInfo.getDatePattern())){
				long milltimes = DateUtil.fixedDateMilliseconds(value.trim(), templateBasicInfo.getDatePattern());
				setField(templateBasicInfo.getProperty(), milltimes, crawlBasicInfo, Long.class); 
			} else {
				setField(templateBasicInfo.getProperty(), value.trim(), crawlBasicInfo, String.class);	
			}
		}
		return true;
	}
	
	private String fixedHtmlContent(String action, String expression, String htmlContent, Document document) {
		String value = null;
		if("regular".equals(action)){
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(htmlContent);
			if(matcher.find())
				value = matcher.group(1);
		} else if("select".equals(action)){
			JsonArray jsonArray = new JsonParser().parse(expression).getAsJsonArray();
			Element element = document;
			boolean hasExclude = false;
			boolean hasEnd = false;
			String appendValue = "";
			for(int i=0;i<jsonArray.size();i++){
				JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
				String jsonAction = jsonObject.get("action").getAsString();
				if("attr".equals(jsonAction)) {
					if (element !=null) {
						element = element.getElementsByAttributeValue(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString()).first();
					}
				} else if("append-attr".equals(jsonAction)) {
					Elements appendEl = document.getElementsByAttributeValue(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString());	
					if (appendEl != null && appendEl.size() > 0) {
						appendValue = appendValue + appendEl.html();
					}
				} else if("tag".equals(jsonAction)) {
					if (element !=null) {
						element = element.getElementsByTag(jsonObject.get("name").getAsString()).first();
					}
				} else if("append-tag".equals(jsonAction)) {
					Elements appendEl = document.getElementsByTag(jsonObject.get("name").getAsString());
					if (appendEl != null && appendEl.size() > 0) {
						appendValue = appendValue + appendEl.html();
					}
				} else if("reg".equals(jsonAction)) {
					Pattern exPattern = Pattern.compile(jsonObject.get("value").getAsString());
					if (element != null) {
						Matcher matcher = exPattern.matcher(element.html());
						if(matcher.find()) {
							if (matcher.groupCount() > 0) {
								return matcher.group(1);					
							} else {
								return matcher.group();
							}
						}
					}
				} else if("ex-attr".equals(jsonAction) || "ex-tag".equals(jsonAction) || "ex-reg".equals(jsonAction)) {
					hasExclude = true;
				} else if("end-attr".equals(jsonAction) || "end-tag".equals(jsonAction) || "end-reg".equals(jsonAction)) {
					hasEnd = true;
				} else if("alter-attr".equals(jsonAction)) {
					if (element == null) {
						element = document.getElementsByAttributeValue(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString()).first();	
					}
				} else if("alter-tag".equals(jsonAction)) {
					if (element == null) {
						element = document.getElementsByTag(jsonObject.get("name").getAsString()).first();	
					}
				}
//				if(element == null)
//					break;
			}
			if (element == null) {
				return null;
			}
			Element valueDoc = element.clone();
			if (hasEnd) {
				for(int i=0;i<jsonArray.size();i++) {
					JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
					String jsonAction = jsonObject.get("action").getAsString();
					if("end-attr".equals(jsonAction)) {
						Elements exElments = valueDoc.getElementsByAttributeValue(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString());	
						if (exElments == null || exElments.size() == 0) {
							continue;
						}
						int preIdx = 0;
						if (jsonObject.get("pre-idx") != null) {
							preIdx = jsonObject.get("pre-idx").getAsInt();							
						}
						int idx = 0;
						if (preIdx > 0 ) {
							idx = exElments.first().parent().elementSiblingIndex();
						} else {
							idx = exElments.first().elementSiblingIndex();
						}
						idx = idx -1;
						idx = idx < 0 ? 0 : idx;
						Elements endElements = valueDoc.getElementsByIndexGreaterThan(idx);
						if (endElements != null && endElements.size() > 0) {
							endElements.remove();
						}
					} else if("end-tag".equals(jsonAction)) {
						Elements exElments = valueDoc.getElementsByTag(jsonObject.get("name").getAsString());
						if (exElments == null || exElments.size() == 0) {
							continue;
						}						
						int idx = exElments.first().elementSiblingIndex();
						idx = idx -1;
						idx = idx < 0 ? 0 : idx;
						Elements endElements = valueDoc.getElementsByIndexGreaterThan(idx);
						if (endElements != null && endElements.size() > 0) {
							endElements.remove();
						}
					} else if("end-reg".equals(jsonAction)) {
						Pattern exPattern = Pattern.compile(jsonObject.get("value").getAsString());
						Elements childs = valueDoc.children();
						int idx = 0;
						boolean hasEx = false;
						for (Element el : childs) {
							Elements exElments = el.getElementsMatchingText(exPattern);
							if (exElments != null && exElments.size() > 0) {
								hasEx = true;
								break;
							}
							idx = idx + 1;
						}
						if (hasEx) {
							idx = idx -1;
							idx = idx < 0 ? 0 : idx;
							Elements endElements = valueDoc.getElementsByIndexGreaterThan(idx);
							if (endElements != null && endElements.size() > 0) {
								endElements.remove();
							}							
						}
					}
				}
			}
			
			if(valueDoc != null) {
				if (!hasExclude) {
					value = valueDoc.html();
				} else {
					if (hasExclude) {
						Element exElement = null;
						for(int i=0;i<jsonArray.size();i++) {
							JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
							String jsonAction = jsonObject.get("action").getAsString();
							if("ex-attr".equals(jsonAction)) {
								Elements exElments = valueDoc.getElementsByAttributeValue(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString());
								int idx = 0;
								if (jsonObject.get("idx") != null) {
									idx = jsonObject.get("idx").getAsInt();
								}
								if (exElments != null && exElments.size() > 0) {
									if (idx == -1) {
										exElments.remove();
									} else {
										exElement = exElments.get(idx);
										if (exElement != null) {
											exElement.remove();
										}
									}
								}
								
							} else if("ex-tag".equals(jsonAction)) {
								Elements exElments = valueDoc.getElementsByTag(jsonObject.get("name").getAsString());
								int idx = 0;
								if (jsonObject.get("idx") != null) {
									idx = jsonObject.get("idx").getAsInt();
								}
								if (exElments != null && exElments.size() > 0) {
									if (idx == -1) {
										exElments.remove();
									} else {
										exElement = exElments.get(idx);
										if (exElement != null) {
											exElement.remove();
										}
									}
								}
								
							} else {
								exElement = null;
							}						
							if(valueDoc == null)
								break;	
							
						}
						value = valueDoc.html();
						for(int i=0;i<jsonArray.size();i++) {
							JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
							String jsonAction = jsonObject.get("action").getAsString();
							if("ex-reg".equals(jsonAction)) {
								Pattern exPattern = Pattern.compile(jsonObject.get("value").getAsString());
								Matcher matcher = exPattern.matcher(value);
								if(matcher.find()) {
									if (matcher.groupCount() > 0) {
										String mat = matcher.group(1);
										value = value.replaceAll(mat, "");
										
									} else {
										value = matcher.replaceAll("");
									}
								}
							}				
						}
					}
				}
				
			}
			value = appendValue + value;
		} else if("substr".equals(action)){
			
		}
		
		return value;
	}
	
	
	private Elements contentExpressionArea(String expression, Document document) {
		JsonArray jsonArray = new JsonParser().parse(expression).getAsJsonArray();
		Elements elements = null;
		for(int i=0;i<jsonArray.size();i++){
			JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
			String jsonAction = jsonObject.get("action").getAsString();
			if("attr".equals(jsonAction)) {
				Elements el  = document.getElementsByAttributeValue(jsonObject.get("key").getAsString(), jsonObject.get("value").getAsString());				
				if (el != null && el.size() > 0) {
					if (elements != null ) {
						elements.addAll(el);
					} else {
						elements = el;
					}		
				}
			} else if("tag".equals(jsonAction)) {
				Elements el = document.getElementsByTag(jsonObject.get("name").getAsString());
				if (el != null && el.size() > 0) {
					if (elements != null ) {
						elements.addAll(el);
					} else {
						elements = el;
					}		
				}
			}
		}
		return elements;
	}	

	private List<CrawlImage> downloadContentImage(Elements elements, String entryWay) {
		List<CrawlImage> crawlImages = new ArrayList<CrawlImage>();
		for(Element imageElement : elements){
			String src = imageElement.attr("abs:src");
			String real_src = imageElement.attr("abs:real_src");
			String alt = imageElement.attr("alt");
			String url = null;
			if(StringUtils.isNotBlank(real_src)){
				url = real_src;
			} else {
				url = src;
			}
			
			if (StringUtils.isBlank(url) || entryWay.equals(url))
				continue;
			
			String urlMd5 = Md5Util.generateMd5ByUrl(url);
			if(urlMd5 == null){
				logger.error("generationUrlMd5Fail: url[{}]", url);
				return null;
			}
			String reffer = null;
			if (entryWay.indexOf("81un") > 0) {
				reffer = entryWay;
			}
			byte[] data = httpClientService.getImageByGet(url, reffer);
			if(data == null){
				logger.error("downloadImageEmpty: url[{}]", url);
				return null;
			}
			
			logger.info("findNewImage: url["+url+"] pageUrl["+entryWay+"] urlMd5["+urlMd5+"]");
			
			imageElement.replaceWith(new TextNode("(key:"+urlMd5+")",""));
			
			CrawlImage crawlImage = new CrawlImage();
			crawlImage.setData(data);
			crawlImage.setUrl(url);
			crawlImage.setMd5(urlMd5);
			crawlImage.setAlt(alt);
			crawlImages.add(crawlImage);
		}
		
		return crawlImages;
	}

	@SuppressWarnings("unchecked")
	private void setField(String name, Object value, CrawlBasicInfo c, Class parameterType) {
		try {
			name = name.substring(0,1).toUpperCase()+name.substring(1);
			Method m = c.getClass().getDeclaredMethod("set" + name, parameterType);
			m.invoke(c, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private int getNewsWeight(Element element, List<TemplateTagWeight> templateTagWeights) {
        int weight = 1;
    	if(templateTagWeights == null)
    		return weight;
    	
        if (null != element.parent()) {
            for (TemplateTagWeight tagWeight : templateTagWeights) {
                if (element.parent().tagName().equals(tagWeight.getTag())) {
                    weight = tagWeight.getWeight();
                    break;
                }
            }
        }
        return weight;
    }

}

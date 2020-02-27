package com.pinyougou.page.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Reference
    private PageService pageService;

    @RequestMapping("/genHtml")
    public String genHtml(Long goodsId){
        try {
            //  第一步：创建一个 Configuration 对象
            Configuration configuration = freemarkerConfig.getConfiguration();
            //第2步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("item.ftl");
            // 第3步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
            Goods goods = pageService.findOne(goodsId);
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
                Map<String,Object> map = new HashMap<>();
                map.put("goods",goods);
                map.put("item",item);
                // 第4步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
                Writer writer = new FileWriter("F:\\item89\\"+item.getId()+".html");
                //第5步：调用模板对象的 process 方法输出文件。
                template.process(map,writer);
                //第6步：关闭流
                writer.close();
            }

            return "success ..............";

        } catch (Exception e) {
            e.printStackTrace();
            return "fail ..............";
        }
//


    }

}

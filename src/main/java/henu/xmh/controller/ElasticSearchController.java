package henu.xmh.controller;

import henu.xmh.pojo.TPoem;
import henu.xmh.service.TpoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("es")
public class ElasticSearchController {
    @Autowired
    private TpoemService tpoemService;


    /**
     * 前台检索的控制方法
     *
     * @param searchString 关键词
     * @param pageNum      当前页
     * @param pageSize     当前页的显示记录数
     * @param model        数据作用域
     * @return
     */
    @RequestMapping("search")
    public String find(String searchString, Integer pageNum, Integer pageSize, Model model) {
        System.out.println(searchString + ":" + pageNum + pageSize);
        Map<String, Object> result = tpoemService.findTPoemBySearchStringForPage(searchString, pageNum, pageSize);
        Long count = (Long) result.get("count");
        long pages = (count % 10 == 0) ? (count / 10) : (count / 10 + 1);
        model.addAttribute("tPoems", (List<TPoem>) result.get("tpomes"));
        model.addAttribute("count", count);//记录数
        model.addAttribute("pageSize", pageSize);//页面大小
        model.addAttribute("pageNum", pageNum);//当前页数
        model.addAttribute("searchString", searchString);
        model.addAttribute("pages", pages);//总页数
        return "front";
    }
}

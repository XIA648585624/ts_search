package henu.xmh.controller;

import henu.xmh.pojo.HotSearch;
import henu.xmh.pojo.TCategory;
import henu.xmh.pojo.TPoem;
import henu.xmh.service.TCategoryService;
import henu.xmh.service.TpoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("poem")
public class TpoemController {
    @Autowired
    private TpoemService tpoemService;
    @Autowired
    private TCategoryService tCategoryService;

    /**
     * @param _search      是否为条件查询（为true即为根据条件分页查询）
     * @param searchField  条件查询的条件属性的name
     * @param searchString 条件查询的条件属性的value
     * @param searchOper   条件查询的条件：如eq为equals(等于)...
     * @param rows         分页查询的一页显示多少条信息
     * @param page         分页查询的当前页码
     * @return
     */
    @RequestMapping("findForPage")
    public Map<String, Object> findAllForPage(Boolean _search, String searchField, String searchString, String searchOper, Integer rows, Integer page) {
        Map<String, Object> result = new HashMap<>();
        Integer records = null;//记录数（一共多少条信息）
        Integer total = null;//页数
        List<TPoem> lists = null;//本页显示的信息
        if (!_search) {//如果不是按条件查询
            lists = tpoemService.findAllForAge(page, rows);//记录信息
            records = tpoemService.getCountForAll(rows);//记录数
            total = (records % rows == 0) ? (records / rows) : (records / rows + 1);//信息页数
        } else {
            lists = tpoemService.findBySerachFiledString(searchField, searchString, searchOper, page, rows);
            records = tpoemService.getCountForSerachFiledString(searchField, searchString, searchOper);
            total = (records % rows == 0) ? (records / rows) : (records / rows + 1);//信息页数
        }
        //所有记录数
        result.put("records", records);
        //本页显示记录条数
        result.put("page", page);
        //页数
        result.put("total", total);
        //本页显示的数据
        result.put("rows", lists);
        return result;
    }

    @RequestMapping("findAllCatepory")
    public void findAllCatepory(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print("<select name='categoryid'>");
        List<TCategory> tCategories = tCategoryService.finaAll();
        tCategories.forEach(c -> {
            writer.print("<option value='" + c.getId() + "'>" + c.getName() + "</option>");
        });
        writer.print("</select>");
        writer.flush();
        writer.close();
    }

    @RequestMapping("alter")
    public void alter(TPoem tPoem, String oper) {
        tpoemService.alterForJg(tPoem, oper);
    }

    //将数据库中的数据全部做一次索引
    @RequestMapping("saveEs")
    public TPoem saveEs() {
        tpoemService.savrEs();
        return new TPoem().setContent("successful");
    }

    //删除所有索引
    @RequestMapping("delEs")
    public TPoem delEs() {
        tpoemService.delEs();
        return new TPoem().setContent("successful");
    }

    //查找所有的热搜榜
    @RequestMapping("findAllHotSearchString")
    public List<HotSearch> findAllHotSearchString() {
        return tpoemService.findHotSearch();
    }
}

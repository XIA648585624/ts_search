package henu.xmh.service.impl;

import henu.xmh.dao.TCategoryMapper;
import henu.xmh.pojo.TCategory;
import henu.xmh.pojo.TCategoryExample;
import henu.xmh.service.TCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TCategoryServiceImpl implements TCategoryService {
    @Autowired
    private TCategoryMapper tCategoryMapper;

    @Override
    public List<TCategory> finaAll() {
        return tCategoryMapper.selectByExample(new TCategoryExample());
    }
}

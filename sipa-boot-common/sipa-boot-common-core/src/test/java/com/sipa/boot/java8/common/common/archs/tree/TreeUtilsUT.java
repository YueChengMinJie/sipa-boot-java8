package com.sipa.boot.java8.common.common.archs.tree;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.archs.tree.model.TreeNode;
import com.sipa.boot.java8.common.archs.tree.model.TreeNodeDetail;
import com.sipa.boot.java8.common.archs.tree.util.TreeUtils;
import com.sipa.boot.java8.common.log.util.Console;
import com.sipa.boot.java8.common.utils.JsonUtils;

/**
 * @author caszhou
 * @date 2021/8/14
 */
public class TreeUtilsUT {
    @Test
    public void testGetTree() {
        Menu menu1 = new Menu("1", "-", "a", 1);
        Menu menu2 = new Menu("2", "-", "b", 2);

        Menu menu3 = new Menu("3", "1", "c", 11);
        Menu menu4 = new Menu("4", "1", "d", 12);
        Menu menu5 = new Menu("5", "2", "e", 21);

        List<Menu> menus = Lists.newArrayList(menu3, menu2, menu1, menu5, menu4);

        List<TreeNode<Menu>> tree = TreeUtils.getTree(menus);

        assertThat(tree).hasSize(2);
        assertThat(tree.get(0).getNodes()).hasSize(2);
        assertThat(tree.get(1).getNodes()).hasSize(1);

        assertThat(tree.get(0).getNodes().get(0).getNodes()).isNull();
        assertThat(tree.get(0).getNodes().get(1).getNodes()).isNull();
        assertThat(tree.get(1).getNodes().get(0).getNodes()).isNull();

        Console.log(JsonUtils.writeValueAsString(new ObjectMapper(), tree));
    }

    @Test
    public void testGetFilterTree() {
        Menu menu1 = new Menu("1", "-", "a", 1);
        Menu menu2 = new Menu("2", "-", "b", 2);

        Menu menu3 = new Menu("3", "1", "c", 11);
        Menu menu4 = new Menu("4", "1", "d", 12);
        Menu menu5 = new Menu("5", "2", "e", 21);

        List<Menu> menus1 = Lists.newArrayList(menu3, menu2, menu1, menu5, menu4);
        List<Menu> menus2 = Lists.newArrayList(menu2, menu3);

        List<TreeNode<Menu>> tree = TreeUtils.getFilterTree(menus1, menus2);

        assertThat(tree).hasSize(2);
        assertThat(tree.get(0).getNodes()).hasSize(1);

        assertThat(tree.get(0).getNodes().get(0).getNodes()).isNull();

        Console.log(JsonUtils.writeValueAsString(new ObjectMapper(), tree));
    }

    public static class Menu implements TreeNodeDetail {
        private String id;

        private String pid;

        private String name;

        private Integer order;

        public Menu(String id, String pid, String name, Integer order) {
            this.id = id;
            this.pid = pid;
            this.name = name;
            this.order = order;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        @Override
        public String getItemId() {
            return getId();
        }

        @Override
        public String getParentId() {
            return getPid();
        }

        @Override
        public Integer getSequence() {
            return getOrder();
        }
    }
}

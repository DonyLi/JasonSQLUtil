package com.jason.sqlutil;

import java.util.ArrayList;
import java.util.List;

public class ConditionBuilder {
    List<Condition> conditions = new ArrayList<>();
    List<Order> orderList = new ArrayList<>();
    Page page;

    public ConditionBuilder addAndCondition(String key, String operator, Object value) {
        Condition condition = new Condition(key, operator, String.valueOf(value), 0);
        conditions.add(condition);
        return this;
    }

    public ConditionBuilder addAndCondition(String key, Object value) {
        return addAndCondition(key, "=", value);
    }

    public ConditionBuilder addOrCondition(String key, String operator, Object value) {
        Condition condition = new Condition(key, operator, String.valueOf(value), 1);
        conditions.add(condition);
        return this;
    }

    public ConditionBuilder addOrCondition(String key, Object value) {
        return addOrCondition(key, "=", value);
    }

    public ConditionBuilder addOrder(String key, int orderType) {
        Order order = new Order(key, orderType);
        orderList.add(order);
        return this;
    }

    public ConditionBuilder setPage(int page, int limitNum) {
        this.page = new Page(page, limitNum);
        return this;

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" where ");
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                builder.append(conditions.get(i).getType() == 0 ? " and " : " or ");
            }
            builder.append(conditions.get(i).toString());
        }
        if (orderList.size() > 0) {
            for (int i = 0; i < orderList.size(); i++) {
                if (i == 0) {
                    builder.append("order by ");
                } else {
                    builder.append(",");
                }
                builder.append(orderList.get(i).toString());
            }
        }
        if (page != null) {
            builder.append(page.toString());
        }
        return builder.toString();
    }
}

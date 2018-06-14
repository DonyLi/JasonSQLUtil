package com.jason.sqlutil;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFinder {

    public static String getTableName(Class<?> aClass) {
        Table table = aClass.getAnnotation(Table.class);
        if (table != null) {
            String tableName = table.name();
            if (tableName.length() == 0) {
                tableName = aClass.getSimpleName();
            }
            return tableName;
        } else {
            throw new IllegalStateException("没有定义此表名");
        }

    }

    public static String createTable(Class<?> aClass) {
        String tableName = getTableName(aClass);
        Field[] fields = aClass.getDeclaredFields();
        if (fields.length > 0) {
            List<String> sentences = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String columeName = column.name();
                    if (columeName.length() == 0) {
                        columeName = field.getName();
                    }
                    Class<?> type = field.getType();
                    if (type == long.class || type == Long.class || type == int.class || type == Integer.class) {
                        sentences.add(columeName + " int");
                    } else if (type == float.class || type == Float.class || type == double.class || type == Double.class) {
                        sentences.add(columeName + " real");
                    } else {
                        sentences.add(columeName + " text");
                    }

                } else {
                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        Class<?> type = field.getType();
                        if (type == long.class || type == Long.class || type == int.class || type == Integer.class) {
                            sentences.add("id integer PRIMARY KEY AUTOINCREMENT");
                        } else {
                            throw new IllegalStateException("Id必须为int或者long类型");
                        }

                    }
                }
            }
            if (sentences.size() > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("create table if not exists ").append(tableName).append("(");
                for (int i = 0; i < sentences.size(); i++) {
                    builder.append(sentences.get(i)).append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append(")");
                return builder.toString();
            } else {
                throw new IllegalStateException("表中没有字段");
            }

        } else {
            throw new IllegalStateException("表中没有定义列");
        }

    }

    public static Map<String, String> getSentenceMap(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields.length > 0) {
            Map<String, String> setenceMap = new HashMap<>();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                try {
                    Object o = field.get(object);
                    Column column = field.getAnnotation(Column.class);
                    if (column != null) {
                        if (o == null) {
                            if (column.notNull()) {
                                if (column.defauleValue().length() == 0) {
                                    throw new IllegalStateException("不允许插入空值");
                                } else {
                                    o = column.defauleValue();
                                }
                            }
                        }
                        String cloumnName = column.name();
                        if (cloumnName.length() == 0) {
                            cloumnName = field.getName();
                        }
                        if (o != null) {
                            setenceMap.put(cloumnName, String.valueOf(o).replace("'", "''"));
                        }
                    } else {
                        Id id = field.getAnnotation(Id.class);
                        if (id != null && o != null) {
                            long idNum = Long.parseLong(String.valueOf(o));
                            if (idNum != 0) {
                                setenceMap.put("id", String.valueOf(idNum));
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return setenceMap;
        } else {
            throw new IllegalStateException("无任何数据可插入");
        }
    }

    //插入数据
    public static String insert(Object object) {
        Class<?> type = object.getClass();
        String tableName = getTableName(type);
        Map<String, String> setenceMap = getSentenceMap(object);

        if (setenceMap.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("insert into ").append(tableName).append("(");
            List<String> values = new ArrayList<>();
            for (String key : setenceMap.keySet()) {
                builder.append(key).append(",");
                values.add(setenceMap.get(key));
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") values (");
            for (int i = 0; i < values.size(); i++) {
                builder.append("'").append(values.get(i)).append("',");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");
            return builder.toString();
        } else {
            throw new IllegalStateException("无任何数据可插入");
        }


    }

    public static String delete(Object object) {
        Class<?> type = object.getClass();
        String tableName = getTableName(type);
        Map<String, String> setenceMap = getSentenceMap(object);
        if (setenceMap.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("delete from ").append(tableName).append(" where ");
            for (String key : setenceMap.keySet()) {
                builder.append(key).append("='").append(setenceMap.get(key)).append("' and ");
            }
            builder.delete(builder.length() - 6, builder.length() - 1);
            return builder.toString();
        } else {
            throw new IllegalStateException("没有设置删除条件");
        }
    }

    public static String update(Object object) {
        Class<?> type = object.getClass();
        String tableName = getTableName(type);
        Map<String, String> setenceMap = getSentenceMap(object);

        if (setenceMap.size() > 0) {
            if (setenceMap.get("id") == null) {
                throw new IllegalStateException("缺少更新Id");
            }
            StringBuilder builder = new StringBuilder();
            builder.append("update ").append(tableName).append(" set ");
            for (String key : setenceMap.keySet()) {
                if (key.equals("id")) {
                    continue;
                }
                builder.append(key).append("= '").append(setenceMap.get(key)).append("',");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(" where id =").append(setenceMap.get("id"));
            return builder.toString();
        } else {
            throw new IllegalStateException("没有设置更新条件");
        }
    }

    public static void selectAll(Object object, Cursor cursor) {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                String columeName = column.name();
                if (columeName.length() == 0) {
                    columeName = field.getName();
                }
                String value = cursor.getString(cursor.getColumnIndex(columeName));
                Class<?> type = field.getType();
                try {
                    if (type == long.class || type == Long.class) {
                        field.set(object, Long.parseLong(value));
                    } else if (type == int.class || type == Integer.class) {
                        field.set(object, Integer.parseInt(value));
                    } else if (type == float.class || type == Float.class) {
                        field.set(object, Float.parseFloat(value));
                    } else if (type == double.class || type == Double.class) {
                        field.set(object, Double.parseDouble(value));
                    } else if (type == String.class) {
                        field.set(object, value);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Id id = field.getAnnotation(Id.class);
                if (id != null) {
                    Class<?> type = field.getType();
                    String value = cursor.getString(cursor.getColumnIndex("id"));
                    try {
                        if (type == long.class || type == Long.class) {
                            field.set(object, Long.parseLong(value));
                        } else if (type == int.class || type == Integer.class) {
                            field.set(object, Integer.parseInt(value));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                }
            }

        }


    }

}

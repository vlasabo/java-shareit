package ru.practicum.shareit.patcher;

import java.util.Objects;

public class ObjectPatcher { //пробегается по всем полям и если они не нулл то меняет значения
    //постарался написать универсальный класс для любого объекта
    public static Object changeFields(Object source, Object receiver) throws IllegalAccessException, NoSuchFieldException {
        var fields = source.getClass().getDeclaredFields();
        for (var field : fields) {
            if (Objects.equals(field.getName(), "id")) { //запретим менять ид
                continue;
            }
            var recField = receiver.getClass().getDeclaredField(field.getName());
            recField.setAccessible(true);
            var sourceField = source.getClass().getDeclaredField(field.getName());
            sourceField.setAccessible(true);

            if (sourceField.get(source) != null) {
                recField.set(receiver, sourceField.get(source));
            }
        }
        return receiver;
    }
}

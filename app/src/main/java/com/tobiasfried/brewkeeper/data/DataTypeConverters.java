package com.tobiasfried.brewkeeper.data;

import com.tobiasfried.brewkeeper.constants.*;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import androidx.room.TypeConverter;

public class DataTypeConverters {

    /**
     * Convert from int to {@link IngredientType}
     *
     * @param code int from [0,2]
     * @return IngredientType
     */
    @TypeConverter
    public static IngredientType toIngredientType(Integer code) {
        if (code == null) {
            return null;
        }
        return IngredientType.get(code);
    }

    /**
     * Convert from {@link IngredientType} to int
     *
     * @param type IngredientType
     * @return int from [0,2]
     */
    @TypeConverter
    public static Integer toInt(IngredientType type) {
        return type.getCode();
    }

    /**
     * Convert from int to {@link TeaType}
     *
     * @param code int from [0,6]
     * @return TeaType
     */
    @TypeConverter
    public static TeaType toTeaType(Integer code) {
        if (code == null) {
            return null;
        }
        return TeaType.get(code);
    }

    /**
     * Convert from {@link TeaType} to int
     *
     * @param type TeaType
     * @return int from [0,6]
     */
    @TypeConverter
    public static Integer toInt(TeaType type) {
        if (type == null) {
            return null;
        }
        return type.getCode();
    }

    /**
     * Convert from int to {@link Stage}
     *
     * @param code int from [1,2]
     * @return Stage
     */
    @TypeConverter
    public static Stage toStage(Integer code) {
        if (code == null) {
            return null;
        }
        if (code == Stage.PRIMARY.getCode()) {
            return Stage.PRIMARY;
        } else if (code == Stage.SECONDARY.getCode()) {
            return Stage.SECONDARY;
        } else return null;
    }

    /**
     * Convert from int to {@link Stage}
     *
     * @param stage Stage
     * @return int from [1,2]
     */
    @TypeConverter
    public static Integer toInt(Stage stage) {
        if (stage == null) {
            return null;
        }
        return stage.getCode();
    }

    /**
     * Convert from long to {@link }
     *
     * @param timestamp in seconds UTC
     * @return Date
     */
    @TypeConverter
    public static ZonedDateTime toZonedDateTime(Long timestamp) {
        return timestamp == null ? null : ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    @TypeConverter
    public static Long toTimestamp(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : zonedDateTime.toEpochSecond();
    }

}

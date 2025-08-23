package com.febrie.rpg.database.service;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * 제네릭 Firestore 서비스 구현
 * 모든 Firestore 서비스의 공통 기능을 제공
 *
 * @param <T> DTO 타입
 * @author Febrie, CoffeeTory
 */
public class GenericFirestoreService<T> extends BaseFirestoreService<T> {

    private final Function<T, Map<String, Object>> toMapFunction;
    private final Function<Map<String, Object>, T> fromMapFunction;
    private final Function<String, T> defaultSupplier;

    /**
     * 생성자
     *
     * @param plugin          플러그인 인스턴스
     * @param firestore       Firestore 인스턴스
     * @param collectionName  컬렉션 이름
     * @param dtoClass        DTO 클래스
     * @param toMapFunction   DTO를 Map으로 변환하는 함수
     * @param fromMapFunction Map을 DTO로 변환하는 함수
     * @param defaultSupplier 기본값 생성 함수
     */
    public GenericFirestoreService(@NotNull RPGMain plugin,
                                    @NotNull Firestore firestore,
                                    @NotNull String collectionName,
                                    @NotNull Class<T> dtoClass,
                                    @NotNull Function<T, Map<String, Object>> toMapFunction,
                                    @NotNull Function<Map<String, Object>, T> fromMapFunction,
                                    @NotNull Function<String, T> defaultSupplier) {
        super(plugin, firestore, collectionName, dtoClass);
        this.toMapFunction = toMapFunction;
        this.fromMapFunction = fromMapFunction;
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    protected Map<String, Object> toMap(@NotNull T dto) {
        return toMapFunction.apply(dto);
    }

    @Override
    @Nullable
    protected T fromDocument(@NotNull com.google.cloud.firestore.DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }

        try {
            Map<String, Object> data = document.getData();
            if (data != null) {
                return fromMapFunction.apply(data);
            }
            return defaultSupplier.apply(document.getId());
        } catch (Exception e) {
            LogUtil.warning(String.format("%s 데이터 파싱 실패 [%s]: %s", 
                collectionName, document.getId(), e.getMessage()));
            return defaultSupplier.apply(document.getId());
        }
    }

    /**
     * 팩토리 메서드 - 간편한 서비스 생성
     */
    public static <T> GenericFirestoreService<T> create(
            @NotNull RPGMain plugin,
            @NotNull Firestore firestore,
            @NotNull String collectionName,
            @NotNull Class<T> dtoClass,
            @NotNull Function<T, Map<String, Object>> toMapFunction,
            @NotNull Function<Map<String, Object>, T> fromMapFunction,
            @NotNull Function<String, T> defaultSupplier) {
        return new GenericFirestoreService<>(
            plugin, firestore, collectionName, dtoClass,
            toMapFunction, fromMapFunction, defaultSupplier
        );
    }
}
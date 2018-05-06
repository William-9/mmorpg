package com.kingston.mmorpg.framework.net.socket.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;

public abstract class Serializer {

	private static Map<Class<?>, Serializer> class2Serializers = new ConcurrentHashMap<>();

	static {
		register(Boolean.TYPE, new BooleanSerializer());
		register(Boolean.class, new BooleanSerializer());
		register(Byte.TYPE, new ByteSerializer());
		register(Byte.class, new ByteSerializer());
		register(Short.TYPE, new ShortSerializer());
		register(Short.class, new ShortSerializer());
		register(Integer.TYPE, new IntSerializer());
		register(Integer.class, new IntSerializer());
		register(Float.TYPE, new FloatSerializer());
		register(Float.class, new FloatSerializer());
		register(Double.TYPE, new DoubleSerializer());
		register(Double.class, new DoubleSerializer());
		register(Long.TYPE, new LongSerializer());
		register(Long.class, new LongSerializer());
		register(String.class, new StringSerializer());
		register(List.class, new CollectionSerializer());
		register(Set.class, new CollectionSerializer());
	}

	public static void register(Class<?> clazz, Serializer serializer) {
		class2Serializers.put(clazz, serializer);
	}

	public static Serializer getSerializer(Class<?> clazz) {
		if (class2Serializers.containsKey(clazz)) {
			return class2Serializers.get(clazz);
		}
		if (clazz.isArray()) {
			return class2Serializers.get(Object[].class);
		}
		Field[] fields = clazz.getDeclaredFields();
		LinkedHashMap<Field, Class<?>> sortedFields = new LinkedHashMap<>();
		List<FieldCodecMeta> fieldsMeta = new ArrayList<>();
		for (Field field: fields) {
			int modifier = field.getModifiers();
			if (Modifier.isFinal(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
				continue;
			}
			field.setAccessible(true);
			sortedFields.put(field, field.getType());
			Class<?> type = field.getType();
			Serializer serializer = Serializer.getSerializer(type);

			fieldsMeta.add(FieldCodecMeta.valueOf(field, serializer));
		}
		Serializer messageCodec = MessageSerializer.valueOf(fieldsMeta);
		Serializer.register(clazz, messageCodec);
		return messageCodec;
	}

	public abstract Object decode(ByteBuf in, Class<?> type, Class<?> wrapper);

	public abstract void encode(ByteBuf out, Object value, Class<?> wrapper);

}

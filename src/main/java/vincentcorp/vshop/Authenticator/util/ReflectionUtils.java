package vincentcorp.vshop.Authenticator.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;


public final class ReflectionUtils 
{
    public static final Gson gson = new Gson();

    /**
     * this method will replace all field value of original to target only if target field value is not null
     * if origin and target are not the same class then return false
     * @param original original object will be replace with tartget
     * @param target 
     * @return true if replace is success else false
     */
    public static boolean replaceValue(Object original, Object target)
    {
        try
        {
            if(original.getClass() != target.getClass())
                return false;

            Field[] originalFields = original.getClass().getDeclaredFields();
            Field[] targetFields = target.getClass().getDeclaredFields();
            
            for (int i = 0; i < originalFields.length; i++) 
            {
                originalFields[i].setAccessible(true);
                targetFields[i].setAccessible(true);

                // Object originalValue = originalFields[i].get(original);
                Object targetValue = targetFields[i].get(target);
                
                if(validAnnotation(targetFields[i].getAnnotations()))
                    originalFields[i].set(original, targetValue);

                originalFields[i].setAccessible(false);
                targetFields[i].setAccessible(false);
            }

            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * this method will patch all field value of original to target only if target field value is not null
     * if origin and target are not the same class then return false
     * @param original original object will be patch with target
     * @param target 
     * @return true if patch is success else false
     */
    public static boolean patchValue(Object original, Object target)
    {
        try
        {
            if(original.getClass() != target.getClass())
                return false;

            Field[] originalFields = original.getClass().getDeclaredFields();
            Field[] targetFields = target.getClass().getDeclaredFields();
            
            for (int i = 0; i < originalFields.length; i++) 
            {
                originalFields[i].setAccessible(true);
                targetFields[i].setAccessible(true);

                // Object originalValue = originalFields[i].get(original);
                Object targetValue = targetFields[i].get(target);

                if(targetValue != null && validAnnotation(targetFields[i].getAnnotations()))
                    originalFields[i].set(original, targetValue);

                originalFields[i].setAccessible(false);
                targetFields[i].setAccessible(false);
            }

            return true;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    private static boolean validAnnotation(Annotation[] annotations)
    {
        for (Annotation annotation : annotations) 
        {
            if(annotation.annotationType() == jakarta.persistence.Id.class)
                return false;
        }
        return true;
    }

    private static boolean containIdAnnotation(Annotation[] annotations) {
        var match = Arrays.stream(annotations).parallel().anyMatch(e -> {
            return e.annotationType() == jakarta.persistence.Id.class;
        });

        return match;
    }

    public static boolean isEqual(Object object1, Object object2)
    {
        return ReflectionUtils.gson.toJson(object1).equals(ReflectionUtils.gson.toJson(object2));
    }

    public static <T extends Object> Example<T> getMatchAllMatcher(T object)
    {
        try
        {
            ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll();

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) 
            {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(object);

                if(fieldValue != null)
                    customExampleMatcher.withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

                field.setAccessible(false);
            }

            return Example.of(object, customExampleMatcher);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T extends Object> Example<T> getMatchAnyMatcher(T object)
    {
        try
        {
            ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny();

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) 
            {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(object);

                if(fieldValue != null)
                    customExampleMatcher.withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

                field.setAccessible(false);
            }

            return Example.of(object, customExampleMatcher);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object getIdFieldValue(Object object) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();

            for (int i = 0; i < fields.length; i++) 
            {
                fields[i].setAccessible(true);
                Object value = fields[i].get(object);
    
                if(!ObjectUtils.isEmpty(value) && containIdAnnotation(fields[i].getAnnotations())) {
                    fields[i].setAccessible(false);
                    return value;
                }
    
                fields[i].setAccessible(false);
            }

            return null;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

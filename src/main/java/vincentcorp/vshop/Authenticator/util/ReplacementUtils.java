package vincentcorp.vshop.Authenticator.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public final class ReplacementUtils 
{

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

                if(targetValue != null && validAnotation(targetFields[i].getAnnotations()))
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

    private static boolean validAnotation(Annotation[] annotations)
    {
        for (Annotation annotation : annotations) 
        {
            if(annotation.annotationType() == jakarta.persistence.Id.class)
                return false;
        }
        return true;
    }
}

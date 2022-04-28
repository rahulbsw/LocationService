package io.github.pantomath.location.pinot;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import io.github.pantomath.location.common.City;
import io.github.pantomath.location.common.Domain;
import io.github.pantomath.location.common.ISP;
import org.apache.pinot.spi.data.ComplexFieldSpec;
import org.apache.pinot.spi.data.DimensionFieldSpec;
import org.apache.pinot.spi.data.FieldSpec;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

public class LocationFieldSpecUtil {

   public static ComplexFieldSpec getLocationFieldSpec(){
       return  getFieldSpec(City.getDefaultInstance().getDescriptorForType(), ISP.getDescriptor(), Domain.getDescriptor());
   }

   public static FieldSpec.DataType getPinotDataType(JavaType type){

       switch (type){
           case INT: return FieldSpec.DataType.INT;
           case LONG: return FieldSpec.DataType.LONG;
           case BOOLEAN: return FieldSpec.DataType.BOOLEAN;
           case FLOAT: return FieldSpec.DataType.DOUBLE;
           case BYTE_STRING: return FieldSpec.DataType.BYTES;
           case MESSAGE: return FieldSpec.DataType.STRUCT;
           case STRING: //default STRING
           case ENUM: //default STRING
           default:
               return FieldSpec.DataType.STRING;
       }
   }

   public static ComplexFieldSpec getFieldSpec(Descriptors.Descriptor... descriptors){
       ComplexFieldSpec complexFieldSpec=new ComplexFieldSpec();
       List<Descriptors.FieldDescriptor> fields = Arrays.stream(descriptors).flatMap(d-> Stream.of(d.getFields()).flatMap(Collection::stream)).collect(Collectors.toList());
       fields.forEach(
       (f)-> {
           if(f.getJavaType().equals(JavaType.MESSAGE))
             complexFieldSpec.addChildFieldSpec(f.getName(), getFieldSpec(f.getMessageType()));
           else if(f.isRepeated())
             complexFieldSpec.addChildFieldSpec(f.getName(),new DimensionFieldSpec(f.getName(), FieldSpec.DataType.LIST,true));
           else
            complexFieldSpec.addChildFieldSpec(f.getName(), new DimensionFieldSpec(f.getName(), getPinotDataType(f.getJavaType()),false));
       }
       );
       return complexFieldSpec;
   }

    public static void main(String[] args) {
        System.out.println(getLocationFieldSpec().getDataType());
    }
}

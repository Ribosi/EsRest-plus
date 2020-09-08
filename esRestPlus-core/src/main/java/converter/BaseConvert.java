package converter;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class BaseConvert<T,A> {
    public abstract A convert(T t);
    public List<A> convertList(List<T> tList){
        return transformer(tList,this::convert);
    }
    public List<A> transformer(List<T> tList, Function<? super T, ? extends A> mapper){
        return Optional.of(tList).orElse(new ArrayList<>()).stream().map(mapper).collect(Collectors.toList());
    }
}

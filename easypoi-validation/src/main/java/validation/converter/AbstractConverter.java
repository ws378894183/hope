package validation.converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractConverter<Model, VO> {

    public abstract Model toModel(VO dto);

    public abstract VO toVO(Model model);

    public final List<Model> toListModel(final List<VO> voList) {
        final List<Model> modelList = voList.stream().map(t -> this.toModel(t)).collect(Collectors.toList());
        return modelList;
    }

    public final List<VO> toListVO(final List<Model> modelList) {
        final List<VO> voList = modelList.stream().map(t -> this.toVO(t)).collect(Collectors.toList());
        return voList;
    }
}

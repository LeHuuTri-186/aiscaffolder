package com.aiscaffolder.projecttemplateengine.mappers;

public interface Mapper<Source, Target> {
    Target mapTo(Source source);
    Source mapFrom(Target target);
}

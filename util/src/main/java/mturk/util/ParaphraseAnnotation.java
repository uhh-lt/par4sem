package mturk.util;

import java.util.Set;

public class ParaphraseAnnotation
{
        private int begin;
        private int end;
        private Set<String> annotations;
        private String source;
        private int id;

        public ParaphraseAnnotation(int aId, String aSource, int aBegin, int aEnd, Set<String> aAnnotations)
        {
            this.begin = aBegin;
            this.end = aEnd;
            this.annotations = aAnnotations;
            this.source = aSource;
            this.id = aId;
        }
}

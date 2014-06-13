package org.metaborg.sunshine.services.analyzer;

public class AnalysisTimeResult {
	public final long parse;
	public final long preTrans;
	public final long collect;
	public final long taskEval;
	public final long postTrans;
	public final long indexPersist;
	public final long taskPersist;

	public AnalysisTimeResult(long parse, long preTrans, long collect,
			long taskEval, long postTrans, long indexPersist, long taskPersist) {
		this.parse = parse;
		this.preTrans = preTrans;
		this.collect = collect;
		this.taskEval = taskEval;
		this.postTrans = postTrans;
		this.indexPersist = indexPersist;
		this.taskPersist = taskPersist;
	}
}

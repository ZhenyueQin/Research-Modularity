package ga.frame.frames;

import com.sun.istack.internal.NotNull;
import ga.collections.Statistics;
import ga.components.chromosomes.Chromosome;
import ga.frame.states.State;
import ga.frame.states.DiploidState;
import ga.operations.dynamicHandlers.DynamicHandler;
import ga.operations.postOperators.PostOperator;
import ga.operations.priorOperators.PriorOperator;

/**
 * Created by zhenyueqin on 17/6/17.
 */
public class SingleObjectDiploidFrame<C extends Chromosome> extends Frame<C> {

    public SingleObjectDiploidFrame(@NotNull final State<C> state,
                                    @NotNull final PostOperator<C> postOperator,
                                    @NotNull final Statistics<C> statistics,
                                    @NotNull final DynamicHandler<C> handler) {
        super(state, postOperator, statistics, handler);
    }

    public SingleObjectDiploidFrame(@NotNull final State<C> state,
                                    @NotNull final PostOperator<C> postOperator,
                                    @NotNull final Statistics<C> statistics) {
        super(state, postOperator, statistics);
    }

    public SingleObjectDiploidFrame(@NotNull final State<C> state,
                                    @NotNull final PostOperator<C> postOperator,
                                    @NotNull final Statistics<C> statistics,
                                    @NotNull final PriorOperator<C> priorOperator) {
        super(state, postOperator, statistics, priorOperator);
    }

    public SingleObjectDiploidFrame(@NotNull final State<C> state,
                                    @NotNull final PostOperator<C> postOperator,
                                    @NotNull final Statistics<C> statistics,
                                    @NotNull final PriorOperator<C> priorOperator,
                                    @NotNull final DynamicHandler<C> handler) {
        super(state, postOperator, statistics, priorOperator, handler);
    }

    @Override
    public void evolve() {
        if (handler != null && handler.handle(state)) {
            statistics.nextGeneration();
            state.record(statistics);
            return;
        }
        if (priorOperator != null)
            state.preOperate(priorOperator);
        state.reproduce();
        state.mutate();
        state.postOperate(postOperator);
        state.nextGeneration();
        state.evaluate(true);
        ((DiploidState) state).mutateExpressionMap();
        statistics.nextGeneration();
        state.record(statistics);
    }
}

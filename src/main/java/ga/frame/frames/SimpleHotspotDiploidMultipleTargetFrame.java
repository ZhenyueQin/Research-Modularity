package ga.frame.frames;

import ga.collections.Statistics;
import ga.components.chromosomes.Chromosome;
import ga.components.chromosomes.Diploid;
import ga.components.chromosomes.WithHotspot;
import ga.frame.states.DiploidState;
import ga.frame.states.HotspotState;
import ga.frame.states.State;
import ga.operations.dynamicHandlers.DynamicHandler;
import ga.operations.postOperators.PostOperator;
import ga.operations.priorOperators.PriorOperator;
import org.jetbrains.annotations.NotNull;

/**
 * Created by zhenyueqin on 22/6/17.
 */
public class SimpleHotspotDiploidMultipleTargetFrame<C extends Chromosome & WithHotspot> extends Frame<C> {
    public SimpleHotspotDiploidMultipleTargetFrame(@NotNull final State<C> state,
                                                   @NotNull final PostOperator<C> postOperator,
                                                   @NotNull final Statistics<C> statistics,
                                                   @NotNull final DynamicHandler<C> handler) {
        super(state, postOperator, statistics, handler);
    }

    public SimpleHotspotDiploidMultipleTargetFrame(@NotNull final State<C> state,
                                                   @NotNull final PostOperator<C> postOperator,
                                                   @NotNull final Statistics<C> statistics) {
        super(state, postOperator, statistics);
    }

    public SimpleHotspotDiploidMultipleTargetFrame(@NotNull final State<C> state,
                                                   @NotNull final PostOperator<C> postOperator,
                                                   @NotNull final Statistics<C> statistics,
                                                   @NotNull final PriorOperator<C> priorOperator) {
        super(state, postOperator, statistics, priorOperator);
    }

    public SimpleHotspotDiploidMultipleTargetFrame(@NotNull final State<C> state,
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
        ((DiploidState<C>) state).mutateExpressionMap();
        ((HotspotState<C>) state).mutateHotspot();
        state.postOperate(postOperator);
        state.nextGeneration();
        statistics.nextGeneration();
        state.evaluateWithMultipleTargets(true);
        state.record(statistics);
    }
}

package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;
import java.util.LinkedList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        this.executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        computationRoot.associativeNesting();
        while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            ComputationNode nextToResolve = computationRoot.findResolvable();
            loadAndCompute(nextToResolve);
        }
        try {
            executor.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {

        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        if (node.getNodeType() == ComputationNodeType.MATRIX) {
            return;
        }
        for (ComputationNode child : node.getChildren()) {
            if (child.getNodeType() != ComputationNodeType.MATRIX) {
                throw new IllegalStateException("Cannot compute node: not all children are resolved.");
            }
        }

        leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());

        if (node.getChildren().size() > 1) {
            rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
        }

        List<Runnable> tasks;

        switch (node.getNodeType()) {
            case ADD:
                tasks = createAddTasks();
                break;
            case MULTIPLY:
                tasks = createMultiplyTasks();
                break;
            case NEGATE:
                tasks = createNegateTasks();
                break;
            case TRANSPOSE:
                tasks = createTransposeTasks();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation: " + node.getNodeType());
        }

        if (tasks != null && !tasks.isEmpty()) {
            executor.submitAll(tasks);
            node.resolve(leftMatrix.readRowMajor());
        }
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector vLeft = leftMatrix.get(i);
            SharedVector vRight = rightMatrix.get(i);
            tasks.add(() -> vLeft.add(vRight));
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector rowFromLeft = leftMatrix.get(i);
            tasks.add(() -> rowFromLeft.vecMatMul(rightMatrix));
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector vLeft = leftMatrix.get(i);
            tasks.add(() -> vLeft.negate());
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        // TODO: return tasks that transpose rows
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            SharedVector vLeft = leftMatrix.get(i);
            tasks.add(() -> vLeft.transpose());
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}

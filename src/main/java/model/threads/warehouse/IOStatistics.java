package model.threads.warehouse;

import model.statistics.StatisticNode;

import java.util.List;

public interface IOStatistics {

     void addStatisticNode(StatisticNode statisticNode);

     List<StatisticNode> getStatisticNode();
}

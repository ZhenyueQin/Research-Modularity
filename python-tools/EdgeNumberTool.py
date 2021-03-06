import CSVFileOpener
from file_processor import get_immediate_subdirectories
import math
import file_processor as fp
import statistics
import numpy as np
from GRNPlotter import GRNPlotter


def count_number_of_edges(a_grn):
    return sum(x != 0 for x in a_grn)


class EdgeNumberTool:
    def __init__(self):
        self.csv_file_opener = CSVFileOpener.CSVFileOpener()

    @staticmethod
    def get_inter_module_edge_number(a_grn):
        grn_side_size = int(math.sqrt(len(a_grn)))
        mid_side_size = (grn_side_size / 2)

        non_inter_genes = set()
        for i in range(0, mid_side_size):
            tmp_cross_index = i
            while (tmp_cross_index < mid_side_size * grn_side_size):
                non_inter_genes.add(tmp_cross_index)
                tmp_cross_index += grn_side_size

        for i in range(mid_side_size, grn_side_size):
            tmp_cross_index = i + mid_side_size * grn_side_size
            while (tmp_cross_index < grn_side_size * grn_side_size):
                non_inter_genes.add(tmp_cross_index)
                tmp_cross_index += grn_side_size

        inter_genes = set(list(range(len(a_grn)))) - non_inter_genes
        edge_no = 0
        for i in inter_genes:
            if a_grn[i] != 0:
                edge_no += 1
        return edge_no

    def get_intra_module_edge_number(self, a_grn):
        return fp.count_number_of_edges(a_grn) - self.get_inter_module_edge_number(a_grn)

    def get_average_inter_module_edges_for_an_experiment(self, root_directory_path, a_type, sample_size):
        grn_phenotypes = fp.get_last_grn_phenotypes(sample_size, a_type, root_directory_path)
        inter_edge_nos = []
        for a_phenotype in grn_phenotypes:
            an_inter_module_edge_no = self.get_inter_module_edge_number(a_phenotype)
            # if an_inter_module_edge_no > 15:
            #     print('Caught an inter module edge larger than 15')
            #     grn_plotter = GRNPlotter()
            #     grn_plotter.draw_a_grn(grn=a_phenotype, is_to_save=False)
            inter_edge_nos.append(an_inter_module_edge_no)
        return inter_edge_nos

    def get_average_intra_module_edges_for_an_experiment(self, root_directory_path, a_type, sample_size):
        grn_phenotypes = fp.get_last_grn_phenotypes(sample_size, a_type, root_directory_path)
        inter_edge_nos = []
        for a_phenotype in grn_phenotypes:
            an_intra_module_edge_no = self.get_intra_module_edge_number(a_phenotype)
            # if an_intra_module_edge_no > 15:
            #     print('Caught an inter module edge larger than 15')
            #     grn_plotter = GRNPlotter()
            #     grn_plotter.draw_a_grn(grn=a_phenotype, is_to_save=False)
            inter_edge_nos.append(an_intra_module_edge_no)
        return inter_edge_nos

    def analyze_edge_nums_for_an_experiment(self, root_directory_path, a_type, sample_size, eva_type,
                                            start_gen, end_gen, edge_type='inter_module'):
        grn_all_gen_phenotypes = fp.get_grn_phenotypes(sample_size, a_type, root_directory_path, start_gen, end_gen)
        eva_results = []
        for grn_a_gen_phenotypes in grn_all_gen_phenotypes:
            edge_numbers = []
            for a_grn_g_gen_phenotype in grn_a_gen_phenotypes:
                if edge_type == 'inter_module':
                    edge_numbers.append(self.get_inter_module_edge_number(a_grn_g_gen_phenotype))
                elif edge_type == 'original':
                    edge_numbers.append(count_number_of_edges(a_grn_g_gen_phenotype))
            if eva_type == 'avg':
                eva_results.append(np.mean(edge_numbers))
            elif eva_type == 'mode':
                eva_results.append(statistics.mode(edge_numbers))
            elif eva_type == 'stddev':
                eva_results.append(statistics.stdev(edge_numbers))
            elif eva_type == 'list':
                eva_results.append(edge_numbers)
            else:
                raise RuntimeError('not supported evaluation type. ')
        return eva_results

    def get_average_edge_number_in_each_generation(self, working_path, edge_type='avg'):
        average_edge_numbers = self.csv_file_opener.get_column_values_of_an_trial(working_path, 'AvgEdgeNumber')
        return average_edge_numbers
    
    def get_std_dev_edge_number_in_each_generation(self, working_path):
        std_dev_numbers = self.csv_file_opener.get_column_values_of_an_trial(working_path, 'StdDevEdgeNumber')
        return std_dev_numbers

    def get_avg_edge_num_in_each_generation_of_an_exp(self, exp_path, with_stdev=False, dir_num=-1):
        trial_dirs = fp.get_immediate_subdirectories(exp_path)
        exp_edge_nums = []
        for a_trial_dir in trial_dirs[:dir_num]:
            a_dir_gen_edge_nums = self.get_average_edge_number_in_each_generation(a_trial_dir)
            exp_edge_nums.append(np.array(a_dir_gen_edge_nums))
        exp_edge_nums = np.array(exp_edge_nums)
        avg_exp_edge_nums = np.median(exp_edge_nums, axis=0)
        if not with_stdev:
            return avg_exp_edge_nums
        else:
            std_exp_edge_nums = np.std(exp_edge_nums, axis=0)
            return avg_exp_edge_nums, std_exp_edge_nums

    def get_average_edge_number_of_the_whole_trial(self, working_path):
        edge_numbers = self.get_average_edge_number_in_each_generation(working_path)
        if edge_numbers is None:
            return None
        try:
            return reduce(lambda x, y: x + y, edge_numbers) / len(edge_numbers)
        except RuntimeError or TypeError:
            return None

    def get_average_edge_number_std_dev_of_the_whole_trial(self, working_path):
        std_devs = self.get_std_dev_edge_number_in_each_generation(working_path)
        try:
            return reduce(lambda x, y: x + y, std_devs) / len(std_devs)
        except RuntimeError:
            return 0

    def get_average_edge_number_of_the_whole_experiment(self, working_path):
        avg_edge_numbers = []

        directories = get_immediate_subdirectories(working_path)
        for a_directory in directories:
            try:
                a_new_number = self.get_average_edge_number_of_the_whole_trial(a_directory)
                if a_new_number is not None:
                    avg_edge_numbers.append(a_new_number)
            except RuntimeError or TypeError:
                pass

        return avg_edge_numbers

    def get_average_std_dev_edge_number_of_the_whole_experiment(self, working_path):
        avg_edge_numbers = []

        directories = get_immediate_subdirectories(working_path)
        for a_directory in directories:
            try:
                avg_edge_numbers.append(self.get_average_edge_number_std_dev_of_the_whole_trial(a_directory))
            except RuntimeError:
                pass

        return avg_edge_numbers


if __name__ == '__main__':
    edge_num_tool = EdgeNumberTool()
    prop_dir = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/jars/generated-outputs/proportional-100'
    tour_dir = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/jars/generated-outputs/tournament-100'
    prop_to_tour_dir = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/jars/generated-outputs/prop-to-tour-at' \
                       '-gen-10'
    prop_to_prop_dir = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/jars/generated-outputs/prop-to-prop-at' \
                       '-gen-10'

    tour_1_dir = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/generated-outputs/tour-size-1-gen-300'
    tour_2_dir = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/generated-outputs/tour-size-99-gen-300'

    prop_300 = '/home/zhenyue-qin/Research/Project-Maotai-Modularity/generated-outputs/prop-gen-300'

    prop_gen_edges, prop_gen_edges_stds = edge_num_tool.get_avg_edge_num_in_each_generation_of_an_exp(prop_300, True,
                                                                                                      dir_num=100)
    tour_gen_edges, tour_gen_edges_stds = edge_num_tool.get_avg_edge_num_in_each_generation_of_an_exp(tour_dir, True,
                                                                                                      dir_num=100)
    conv_p_t_gen_edges, conv_p_t_gen_edges_stds = edge_num_tool.get_avg_edge_num_in_each_generation_of_an_exp(
        prop_to_tour_dir, True, dir_num=100)
    conv_p_p_gen_edges, conv_p_p_gen_edges_stds = edge_num_tool.get_avg_edge_num_in_each_generation_of_an_exp(
        prop_to_prop_dir, True, dir_num=100)

    tour_1_edges, tour_1_edges_stds = edge_num_tool.get_avg_edge_num_in_each_generation_of_an_exp(
        tour_1_dir, True, dir_num=4
    )
    tour_2_edges, tour_2_edges_stds = edge_num_tool.get_avg_edge_num_in_each_generation_of_an_exp(
        tour_2_dir, True, dir_num=4
    )

    elite_edge_lists = edge_num_tool.analyze_edge_nums_for_an_experiment(tour_1_dir, a_type='fit', sample_size=100,
                                                            eva_type='list', start_gen=0, end_gen=300,
                                                            edge_type='original')
    np_mean_elite_edge_lists = np.mean(elite_edge_lists, axis=0)
    np_std_elite_edge_lists = np.std(elite_edge_lists, axis=0)

    fp.save_lists_graph([tour_1_edges[:300], tour_2_edges[:300]],
                        error_bars=[tour_1_edges_stds, tour_2_edges_stds])

    # fp.save_lists_graph([prop_gen_edges[:99], tour_gen_edges[:99]], dpi=100)

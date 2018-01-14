clear;
files = {'Q2', 'Q3-X5', 'Q3-X5000', 'Q4-X5', 'Q4-X5000', 'Q5-X5', 'Q5-X5000', 'Q6-X5', 'Q6-X5000', 'Q7-X5', 'Q7-X5000'}; 
MAX_THREADS = 16;
datum = {};
for i = 1:size(files, 2)
    filename = [files{i} '.csv'];
    datum{i} = csvread(filename, 1, 0);
end

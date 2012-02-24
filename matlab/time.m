%CIG - Single Pass - Mod Single Pass, Average Linkage, Complete Linkage,
%Single Linkage
yBar=[65 906 54 827 368;
      115 1425 111 900 474;
      89 1002 70 870 443;
      415 2268 129 1433 765;
      467 1667 123 1432 760;
      446 1552 135 1463 735
      ]
bar(yBar);
title('Times of max f-measures taken for the five Datasets For different Algorithm');
leg = legend('UW-CAN','4 Universities', 'Syskill', 'Reuters', '20 News Group');
set(gca,'XTickLabel',{'CIG','SP','MSP', 'Avg. Link', 'Comp. Link', 'Sin. Link'},  'FontSize', 8) 
xlabel('Algorithms');
ylabel('Time [sec]');

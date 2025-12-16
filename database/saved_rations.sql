USE [CalorieCalculatorDB]
GO

/****** Object:  Table [dbo].[saved_rations]    Script Date: 2025-12-16 2:25:17 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[saved_rations](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NOT NULL,
	[name] [nvarchar](200) NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[saved_rations] ADD  DEFAULT (sysdatetime()) FOR [created_at]
GO

ALTER TABLE [dbo].[saved_rations]  WITH CHECK ADD  CONSTRAINT [FK_saved_rations_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[users] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[saved_rations] CHECK CONSTRAINT [FK_saved_rations_user]
GO


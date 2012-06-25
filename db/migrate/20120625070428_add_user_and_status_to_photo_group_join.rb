class AddUserAndStatusToPhotoGroupJoin < ActiveRecord::Migration
  def change
    add_column :photo_group_joins, :user_id, :integer

    add_column :photo_group_joins, :status, :boolean, :default => false

  end
end

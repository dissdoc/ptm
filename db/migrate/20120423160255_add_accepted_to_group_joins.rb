class AddAcceptedToGroupJoins < ActiveRecord::Migration
  def change
    add_column :group_joins, :accepted, :boolean, :default => false

  end
end

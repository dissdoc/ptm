class AddAgreeToGroupJoin < ActiveRecord::Migration
  def change
    add_column :group_joins, :agree, :boolean, :default => false

  end
end

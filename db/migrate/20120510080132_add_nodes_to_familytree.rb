class AddNodesToFamilytree < ActiveRecord::Migration
  def change
    add_column :familytrees, :left, :integer, :default => 0
    add_column :familytrees, :right, :integer, :default => 0
  end
end

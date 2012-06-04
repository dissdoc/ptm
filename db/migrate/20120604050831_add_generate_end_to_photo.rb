class AddGenerateEndToPhoto < ActiveRecord::Migration
  def change
    add_column :photos, :generate_end, :datetime

  end
end
